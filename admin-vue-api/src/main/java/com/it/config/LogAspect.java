package com.it.config;

import com.it.entity.Log;
import com.it.entity.User;
import com.it.service.LogService;
import com.it.service.UserService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import com.it.util.JwtUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 在标注了 {@link Operation} 的 Controller 方法上记录操作日志（成功/失败写入 sys_log）。
 */
@Component
@Aspect
public class LogAspect {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserService userService;
    private final LogService logService;

    public LogAspect(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    @Pointcut("@annotation(com.it.config.Operation)")
    private void pointCut() {
    }

    @Around("pointCut()")
    public Object recordLog(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Operation annotation = method.getAnnotation(Operation.class);
        if (annotation == null || !CommonUtils.stringIsNotBlack(annotation.value())) {
            return pjp.proceed();
        }

        String operatorName = resolveOperatorName();

        Log systemLog = new Log();
        systemLog.setOperation(annotation.value());
        systemLog.setName(operatorName);
        systemLog.setCreate_time(DateUtil.getNowDateSS());
        try {
            Object result = pjp.proceed();
            systemLog.setOperation_result("0");
            logService.insert(systemLog);
            return result;
        } catch (Throwable e) {
            systemLog.setOperation_result("1");
            String msg = e.getMessage();
            if (msg == null) {
                msg = e.getClass().getSimpleName();
            } else if (msg.length() > 2000) {
                msg = msg.substring(0, 2000);
            }
            systemLog.setOperation_fail_reason(msg);
            logService.insert(systemLog);
            throw e;
        }
    }

    private String resolveOperatorName() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return "未知用户";
            }
            HttpServletRequest request = attrs.getRequest();
            String token = request.getHeader("Authorization");
            if (!CommonUtils.stringIsNotBlack(token)) {
                return "未知用户";
            }
            if (!jwtUtil.verify(token) || jwtUtil.isExpired(token)) {
                return "未知用户";
            }
            String account = jwtUtil.getAccount(token);
            User user = userService.selectByPrimaryKey(account);
            if (user != null && CommonUtils.stringIsNotBlack(user.getUsername())) {
                return user.getUsername();
            }
            return CommonUtils.stringIsNotBlack(account) ? account : "未知用户";
        } catch (Exception e) {
            return "未知用户";
        }
    }
}
