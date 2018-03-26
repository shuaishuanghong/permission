package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.commom.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.model.SysAcl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    public List<SysAcl> getCurrentUserList(){
        Integer userid=RequestHolder.getCurrentUser().getId();
        return getUserAclList(userid);
    }

    public List<SysAcl> getCurrentRoleList(int roleId){
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdLsit(Lists.<Integer>newArrayList(roleId));
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }

    public List<SysAcl> getUserAclList(Integer userId){
        if(isSuperAdmin()){
            return  sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdLsit(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }

    public  boolean isSuperAdmin(){
        return  true;
    }
}
