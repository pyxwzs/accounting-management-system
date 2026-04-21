package com.it.util;

import com.it.entity.User;
import com.it.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述：〈工具类〉
 */
@Component
public class AuthUserUtils {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    /**
     * 获取登录用户的id
     *
     * @return
     */
    public String getLoginUserId() {
        // 拿到请求对象Request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("Authorization");
        //获取到用户id
        String account = jwtUtil.getAccount(token);
        return account;
    }

    /**
     * 获取登录用户的账号
     *
     * @return
     */
    public String getLoginUserName() {
        // 拿到请求对象Request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("Authorization");
        //获取到用户id
        String account = jwtUtil.getAccount(token);
        User user = userService.selectByPrimaryKey(account);
        return user.getUsername();
    }

    /**
     * 获取登录用户全部信息
     *
     * @return
     */
    public User getLoginUser() {
        // 拿到请求对象Request
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("Authorization");
        //获取到用户id
        String account = jwtUtil.getAccount(token);
        User user = userService.selectByPrimaryKey(account);
        return user;
    }

    /**
     * 字符串不为空的判断函数
     *
     * @param string
     * @return
     */
    public static boolean stringIsNotBlack(String string) {
        if (string != null && !"".equals(string)) {
            return true;
        }
        return false;
    }
}
