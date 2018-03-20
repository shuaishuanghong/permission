package com.mmall.commom;


import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class SpringExceptionResolver  implements HandlerExceptionResolver{

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String url =httpServletRequest.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg="System error";
        if(url.endsWith(".json")){
            if(e instanceof PermissionException ||e instanceof ParamException){
                JsonData result=JsonData.fail(e.getMessage());
                mv=new ModelAndView("jsonView",result.toMap());
            }else {
                log.error("unknow  json exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                mv=new ModelAndView("jsonView",result.toMap());
            }
        }else if (url.endsWith(".page")){
                log.error("unknow  page exception,url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg);
                mv=new ModelAndView("exception",result.toMap());
        }else {
            JsonData result=JsonData.fail(defaultMsg);
            log.error("unknow  page exception,url:"+url,e);
            mv=new ModelAndView("jsonView",result.toMap());
        }

        return mv;
    }
}
