package com.mmall.controller;

import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.commom.JsonData;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam param){
        sysUserService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(UserParam param){
        sysUserService.update(param);
        return JsonData.success();
    }
    @RequestMapping("/page.json")
    @ResponseBody
    public  JsonData page(@RequestParam("deptId") Integer depId, PageQuery pageQuery){
             PageResult<SysUser> result=sysUserService.getPageByDeptId(depId,pageQuery);
             return JsonData.success(result);
    }
}
