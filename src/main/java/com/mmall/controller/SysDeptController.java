package com.mmall.controller;

import com.mmall.commom.JsonData;
import com.mmall.param.DeptParam;
import com.mmall.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {
    @Resource
    private SysDeptService sysDeptService;
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam param){
        sysDeptService.save(param);
        return JsonData.success();
    }
}
