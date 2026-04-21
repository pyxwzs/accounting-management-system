package com.it.filter;

import com.alibaba.fastjson.JSON;
import com.it.config.JwtToken;
import com.it.util.CommonUtils;
import com.it.util.DataResult;
import com.it.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * jwt过滤器，作为shiro的过滤器，对请求进行拦截并处理
 */
@Slf4j
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private String errorMsg;


    /**
     * 过滤器拦截请求的入口方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 判断请求头是否带上token
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        // token不存在，禁止访问
        if (!CommonUtils.stringIsNotBlack(token)) {
            return false;
        }
        try {
            // 交给自定义 Realm
            // getSubject(request, response).login(new JwtToken(token));  //getSubject(request, response) 等同于 SecurityUtils.getSubject()
            SecurityUtils.getSubject().login(new JwtToken(token));
            return true;
        } catch (Exception e) {
//            errorMsg = e.getMessage();
//            e.printStackTrace();
            return false;
        }
    }

    /**
     * isAccessAllowed()方法返回false，即认证不通过时进入onAccessDenied方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.setStatus(400);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        DataResult result = DataResult.fail(ResponseCode.TOKEN_ERROR.getCode(), ResponseCode.TOKEN_ERROR.getMessage());
        out.println(JSON.toJSON(result));
        out.flush();
        out.close();
        return false;
    }

    /**
     * 对跨域访问提供支持
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域发送一个option请求
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
