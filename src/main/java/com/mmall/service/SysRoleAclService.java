package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.commom.RequestHolder;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.model.SysRoleAcl;
import com.mmall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class SysRoleAclService {
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    public  void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
            List<Integer> originAclIdList=sysRoleAclMapper.getAclIdListByRoleIdLsit(Lists.newArrayList(roleId));
            if(originAclIdList.size()==aclIdList.size()){
                Set<Integer> originAclIdSet= Sets.newHashSet(originAclIdList);
                Set<Integer> aclIdSet= Sets.newHashSet(aclIdList);
                originAclIdSet.removeAll(aclIdSet);
                if(CollectionUtils.isEmpty(originAclIdSet)){
                    return;
                }
            }
         updateRoleAcls(roleId,aclIdList);
    }
    @Transactional
    public  void updateRoleAcls(int roleId,List<Integer> aclList){
           sysRoleAclMapper.deleteByRoleId(roleId);
           if(CollectionUtils.isEmpty(aclList)){
               return;
           }
           List<SysRoleAcl> roleAcls=Lists.newArrayList();
        for(Integer aclId : aclList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentHttpServletRequest())).operateTime(new Date()).build();
            roleAcls.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAcls);

    }
}
