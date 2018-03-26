package com.mmall.controller;

import com.google.common.collect.Lists;
import com.mmall.commom.JsonData;
import com.mmall.model.SysUser;
import com.mmall.param.RoleParam;
import com.mmall.service.*;
import com.mmall.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysTreeService sysTreeService;
    @Resource
    private SysRoleAclService sysRoleAclService;

    @Resource
    private SysRoleUserService sysRoleUserService;

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("role.page")
    public ModelAndView page() {
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param) {
        sysRoleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param) {
        sysRoleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list() {
        return JsonData.success(sysRoleService.getAll());
    }

    @RequestMapping("roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId") int roleId){
        return JsonData.success( sysTreeService.roleTree(roleId));
    }

    @RequestMapping("changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") int roleId,@RequestParam(value = "aclIds",required = false,defaultValue ="") String aclIds){
        List<Integer> ids= StringUtil.splitToListInt(aclIds);
        sysRoleAclService.changeRoleAcls(roleId,ids);
        return JsonData.success();
    }

    @RequestMapping("changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam("roleId") int roleId,@RequestParam(value = "userIds",required = false,defaultValue ="") String userIds){
        List<Integer> ids= StringUtil.splitToListInt(userIds);
        sysRoleUserService.changeRoleUsers(roleId,ids);
        return JsonData.success();
    }

    @RequestMapping("users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") int roleId){
       List<SysUser> slelectedUserList=sysRoleUserService.getListByRoleId(roleId);
       List<SysUser>  allSysUser= sysUserService.getAll();
       List<SysUser> unslelcetUser= Lists.newArrayList();

       Set<Integer> slelectedUserSet=slelectedUserList.stream().map(x->x.getId()).collect(Collectors.toSet());
       for(SysUser sysUser:allSysUser){
           if(sysUser.getStatus()==1&&!slelectedUserSet.contains(sysUser.getId())){
               unslelcetUser.add(sysUser);
           }
       }
       Map<String,List<SysUser>> map=new HashMap<>();
       map.put("selected",slelectedUserList);
       map.put("unselected",unslelcetUser);
        return JsonData.success(map);
    }

}
