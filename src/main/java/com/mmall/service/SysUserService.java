package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.commom.RequestHolder;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    public  void save(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话以被占用");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱也被占用");
        }
        String password="123";
        String encrptedPassword= MD5Util.encrypt(password);
        SysUser user=SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                .password(encrptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentHttpServletRequest()));
        user.setOperateTime(new Date());
        //TODO:sendEmail
        sysUserMapper.insertSelective(user);
    }

    public  void update(UserParam param){
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话以被占用");
        }
        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱也被占用");
        }
        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");

        SysUser after=SysUser.builder().id(param.getId()).mail(param.getMail()).username(param.getUsername()).telephone(param.getTelephone())
                .password(before.getPassword()).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentHttpServletRequest()));
        after.setOperateTime(new Date());
            sysUserMapper.updateByPrimaryKeySelective(after);

    }

    public  boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId)>0;
    }
    public  boolean checkTelephoneExist(String mail,Integer userId){
        return sysUserMapper.countByTelephone(mail,userId)>0;
    }

    public  SysUser findUserByKeyWord(String keyword){
            return sysUserMapper.findUserByKeyWord(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptid, PageQuery page){
        BeanValidator.check(page);
        int count=sysUserMapper.countByDeptId(deptid);
        if(count>0){
            List<SysUser> list=sysUserMapper.getPageByDeptId(deptid,page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }

}
