package com.mmall.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.mmall.commom.ApplicationContextHelper;
import com.mmall.commom.JsonData;
import com.mmall.commom.RequestHolder;
import com.mmall.model.SysUser;
import com.mmall.service.SysCoreService;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AclControlFilter implements Filter {

    private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();

    private final static String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionUrlSet.add(noAuthUrl);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath(); // "/admin/index.page"
        String servletPath2 = request.getRequestURI();//  "/ssm/admin/index.page"
        Map requestMap = request.getParameterMap();
        //String ss=request.getQueryString();获取get请求后拼的参数，表单请求获取为null

        if (exclusionUrlSet.contains(servletPath)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SysUser sysUser = RequestHolder.getCurrentUser();
        if (sysUser == null) {
            log.info("someone visit {}, but no login, parameter:{}", servletPath, JsonMapper.obj2String(requestMap));
            try {
                noAuth(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if (!sysCoreService.hasUrlAcl(servletPath)) {
            log.info("{} visit {}, but no login, parameter:{}", JsonMapper.obj2String(sysUser), servletPath, JsonMapper.obj2String(requestMap));
            try {
                noAuth(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String servletPath = request.getServletPath();
        if (servletPath.endsWith(".json")) {
            JsonData jsonData = JsonData.fail("没有访问权限，如需要访问，请联系管理员");
            response.setHeader("Content-Type", "application/json");
            response.getWriter().print(JsonMapper.obj2String(jsonData));
            return;
        } else {
            clientRedirect(noAuthUrl, response,request);
            return;
        }
    }

    private void clientRedirect(String url, HttpServletResponse response,HttpServletRequest request) throws Exception{
        response.setHeader("Content-Type", "text/html");
        /*  HttpServletRequest request = (HttpServletRequest) servletRequest;*/
        /*response.sendRedirect("/sys/user/noAuth.page");  //这样是可以的*/
        request.getRequestDispatcher("/sys/user/noAuth.page").forward(request,response);
       /* response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");*/
    }

    @Override
    public void destroy() {

    }
}
