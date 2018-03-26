package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclDto;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptlevelDto;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.levelutil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeService {

     @Resource
     private SysDeptMapper sysDeptMapper;

     @Resource
     private SysAclModuleMapper sysAclModuleMapper;

     @Resource
     private  SysCoreService sysCoreService;

     @Resource
     private SysAclMapper sysAclMapper;


    public List<AclModuleLevelDto> userAclTree(int userId){
        //当前用户的acl
        List<SysAcl> userAclList=sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList=Lists.newArrayList();
        for(SysAcl sysAcl:userAclList){
            AclDto aclDto= AclDto.adapt(sysAcl);
            aclDto.setHasAcl(true);
            aclDto.setChecked(true);
            aclDtoList.add(aclDto);
        }
        return  aclListTotree(aclDtoList);
    }

     public List<AclModuleLevelDto> roleTree(int roleId){
            //当前用户的acl
         List<SysAcl> userAclList=sysCoreService.getCurrentUserList();
           //当前角色的权限id
         List<SysAcl> roleAclList=sysCoreService.getCurrentRoleList(roleId);

         List<SysAcl> allAclList=sysAclMapper.getAll();

         Set<Integer> userAclSet=userAclList.stream().map(x -> x.getId()).collect(Collectors.toSet());
         Set<Integer> roleAclSet=roleAclList.stream().map(x -> x.getId()).collect(Collectors.toSet());

         Set<SysAcl> aclSet=new HashSet<>(allAclList);

         List<AclDto> aclDtoList=Lists.newArrayList();
         for(SysAcl sysAcl:aclSet){
            AclDto aclDto= AclDto.adapt(sysAcl);
            if(userAclSet.contains(sysAcl.getId())){
                aclDto.setHasAcl(true);
            }
            if(roleAclSet.contains(sysAcl.getId())){
                aclDto.setChecked(true);
            }
             aclDtoList.add(aclDto);
         }
         return  aclListTotree(aclDtoList);
     }

     public List<AclModuleLevelDto> aclListTotree(List<AclDto> aclDtoList){
                if(CollectionUtils.isEmpty(aclDtoList)){
                    return Lists.newArrayList();
                }
                List<AclModuleLevelDto> aclModuleLevelDtoList=aclModuleTree();
                Multimap<Integer, AclDto> moduleIdAcMap=ArrayListMultimap.create();
                for(AclDto aclDto:aclDtoList){
                    moduleIdAcMap.put(aclDto.getAclModuleId(),aclDto);
                }
                bindAclsWithOrder(aclModuleLevelDtoList,moduleIdAcMap);
                return aclModuleLevelDtoList;
     }

     private  void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList, Multimap<Integer, AclDto> moduleIdAcMap){
                if(CollectionUtils.isEmpty(aclModuleLevelDtoList)){
                    return;
                }
                for(AclModuleLevelDto dto:aclModuleLevelDtoList){
                    List<AclDto> aclDtoList= (List<AclDto>) moduleIdAcMap.get(dto.getId());
                    if(CollectionUtils.isNotEmpty(aclDtoList)){
                        Collections.sort(aclDtoList,(x,y)->x.getSeq()-y.getSeq());
                        dto.setAclList(aclDtoList);
                    }
                    bindAclsWithOrder(dto.getAclModuleList(),moduleIdAcMap);
                }
     }
     public List<DeptlevelDto> deptTree(){
         List<SysDept> deptList =sysDeptMapper.getAllDept();
         List<DeptlevelDto> dtoList= Lists.newArrayList();
         for(SysDept dept:deptList){
             DeptlevelDto dto=DeptlevelDto.adapt(dept);
             dtoList.add(dto);
         }
         return  deptListToTree(dtoList);
     }
     public List<DeptlevelDto> deptListToTree(List<DeptlevelDto> list){
         //TODO
         if(CollectionUtils.isEmpty(list)){
             return Lists.newArrayList();
         }
         //level->[dept1,dept2,....]
         Multimap<String, DeptlevelDto> leveDeptMap= ArrayListMultimap.create();
         List<DeptlevelDto> rootList=Lists.newArrayList();

         for(DeptlevelDto dto:list){
             leveDeptMap.put(dto.getLevel(),dto);
             if(levelutil.ROOT.equals(dto.getLevel())){
                 rootList.add(dto);
             }
         }
         //按照seq排序
         Collections.sort(rootList, new Comparator<DeptlevelDto>() {
             @Override
             public int compare(DeptlevelDto o1, DeptlevelDto o2) {
                 return o1.getSeq()-o2.getSeq();
             }
         });

         /*Collections.sort(rootList,(x,y)->{return x.getSeq()-y.getSeq();});*/
         transformDeptTree(rootList,levelutil.ROOT,leveDeptMap);
         return rootList;
     }

     public  void transformDeptTree(List<DeptlevelDto> deptLevelList,String level,Multimap<String, DeptlevelDto> leveDeptMap){
         for (int i = 0; i < deptLevelList.size(); i++) {
             DeptlevelDto dto = deptLevelList.get(i);
             String nextLevel = levelutil.calculateLevel(level, dto.getId());
             List<DeptlevelDto> tempList = (List<DeptlevelDto>) leveDeptMap.get(nextLevel);
             if (CollectionUtils.isNotEmpty(tempList)) {
                 Collections.sort(tempList, deptSeqComparator);
                 dto.setDeptList(tempList);
                 transformDeptTree(tempList, nextLevel, leveDeptMap);
             }
         }

     }

    public Comparator<DeptlevelDto> deptSeqComparator = new Comparator<DeptlevelDto>() {
        public int compare(DeptlevelDto o1, DeptlevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

     public List<AclModuleLevelDto> aclModuleTree(){
         List<SysAclModule> aclModuleList=sysAclModuleMapper.getAllAclModule();
         List<AclModuleLevelDto> dtoList=Lists.newArrayList();
         for(SysAclModule sysAclModule:aclModuleList){
                dtoList.add(AclModuleLevelDto.adapt(sysAclModule));
         }

         return aclModuleListToTree(dtoList);

     }

     public  List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> list){
         if(CollectionUtils.isEmpty(list)){
             return  Lists.newArrayList();
         }
         Multimap<String, AclModuleLevelDto> levelDtoMultiMap=ArrayListMultimap.create();

         List<AclModuleLevelDto> rootList=Lists.newArrayList();
         for(AclModuleLevelDto aclModuleLevelDto:list){
             levelDtoMultiMap.put(aclModuleLevelDto.getLevel(),aclModuleLevelDto);
             if(levelutil.ROOT.equals(aclModuleLevelDto.getLevel())){
                 rootList.add(aclModuleLevelDto);
             }
         }
         Collections.sort(rootList, new Comparator<AclModuleLevelDto>() {
             @Override
             public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
                 return o1.getSeq()-o2.getSeq();
             }
         });

         transformAclTree(rootList,levelutil.ROOT,levelDtoMultiMap);

         return rootList;
     }

    public  void transformAclTree(List<AclModuleLevelDto> rootList,String level,Multimap<String, AclModuleLevelDto> leveDeptMap){
         for(int i=0;i<rootList.size();i++){
             AclModuleLevelDto dto = rootList.get(i);
             String nextLevel = levelutil.calculateLevel(level, dto.getId());
             List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) leveDeptMap.get(nextLevel);
             if(CollectionUtils.isNotEmpty(tempList)){
                 Collections.sort(tempList, ACLSeqComparator);
                 dto.setAclModuleList(tempList);
                 transformAclTree(tempList,nextLevel,leveDeptMap);
             }
         }
    }

    public Comparator<AclModuleLevelDto> ACLSeqComparator = new Comparator<AclModuleLevelDto>() {
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

}
