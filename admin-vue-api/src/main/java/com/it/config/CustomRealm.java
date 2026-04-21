package com.it.config;

import com.it.entity.Permission;
import com.it.entity.Role;
import com.it.entity.RolePermission;
import com.it.entity.User;
import com.it.service.PermissionService;
import com.it.service.RoleService;
import com.it.service.UserService;
import com.it.util.CommonUtils;
import com.it.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private JwtUtil jwtUtil;

    //这个方法要重写，debug源码得知shiro会判断token的类型是不是自己支持的类型，不重写的话会报错
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {

        return authenticationToken instanceof JwtToken;
    }

    /**
     * @MethodName doGetAuthorizationInfo
     * @Description 权限配置类，用于获取返回用户配置的角色和权限
     * @Param principalCollection
     * @Return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("enter doGetAuthorizationInfo");
        // 获取身份标识
        // getPrimaryPrincipal() 获取的是 doGetAuthenticationInfo() 返回对象 SimpleAuthenticationInfo 的身份标识 SimplePrincipalCollection 对象。
        User principal = (User) principals.getPrimaryPrincipal();
        //查询用户信息
        User user = userService.getUserByUsername(principal.getUsername());
        //查询角色下的所有权限信息
        //通过数据库获取用户角色和权限
        Role role = roleService.selectByPrimaryKey(user.getRoleId());
        // 添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //添加角色
        simpleAuthorizationInfo.addRole(role.getId());
        //添加权限
        for (RolePermission rolePermission : role.getRolePermissionList()) {
            Permission permission = permissionService.selectByPrimaryKey(rolePermission.getItem_id());
            if (permission != null) {
                simpleAuthorizationInfo.addStringPermission(permission.getMark());
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * @return
     * @MethodName doGetAuthenticationInfo
     * @Description 认证配置类，用于获取返回用户的凭证信息（用户名、密码）
     * @Param authenticationToken
     * @Return AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("enter doGetAuthenticationInfo");
        if (!CommonUtils.stringIsNotBlack((String) authenticationToken.getPrincipal())) {
            return null;
        }
        // JwtToken 中重写 getPrincipal 方法
        String token = authenticationToken.getPrincipal().toString();

        if (!jwtUtil.verify(token)) {
            throw new AuthenticationException("token已失效，请重新登录");
        }
        if (jwtUtil.isExpired(token)) {
            throw new AuthenticationException("token已失效，请重新登录");
        }
        //获取到用户id
        String account = jwtUtil.getAccount(token);
        //获取用户信息
        User user = userService.selectByPrimaryKey(account);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                user,  // 身份标识  封装成 SimplePrincipalCollection，传递给 doGetAuthorizationInfo() 方法
                token,  // 凭证
                this.getName()  // Realm 名称
        );
        return simpleAuthenticationInfo;
    }
}
