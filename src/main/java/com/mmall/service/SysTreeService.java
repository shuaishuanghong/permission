package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptlevelDto;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.levelutil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeService {

     @Resource
     private SysDeptMapper sysDeptMapper;

     @Resource
     private SysAclModuleMapper sysAclModuleMapper;

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
