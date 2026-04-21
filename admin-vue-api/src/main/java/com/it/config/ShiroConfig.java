package com.it.config;

import com.it.filter.JwtFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    /**
     * 自定义realm
     */
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        return customRealm;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置自定义 Realm
        securityManager.setRealm(customRealm());
        // 禁用 Shiro 的 Session 存储，这样可以确保 shiro 不会创建或使用 Session，而是依赖于无状态的 Token 来进行认证。
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 注入 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 添加自定义 Filter，并且取名为 jwt
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // authc：所有url都必须认证通过才可以访问; anon：所有url都都可以匿名访问
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/loginFront", "anon");
        filterChainDefinitionMap.put("/user/register", "anon");
        filterChainDefinitionMap.put("/code/sendCode", "anon");
        filterChainDefinitionMap.put("/code/verifyCode", "anon");
        filterChainDefinitionMap.put("/user/resetPwd", "anon");
        filterChainDefinitionMap.put("/category/ptList", "anon");
        filterChainDefinitionMap.put("/slideshow/slideshows", "anon");
        filterChainDefinitionMap.put("/blog/blogs/", "anon");
        filterChainDefinitionMap.put("/article/articles/", "anon");
        filterChainDefinitionMap.put("/article/article/**", "anon");
        //swagger2界面访问放行
        filterChainDefinitionMap.put("/webjars/bycdao-ui/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        //img
        filterChainDefinitionMap.put("/image/**", "anon");
        // /** 必须放在所有权限设置的最后，表示对所有资源起作用，本例使用自定义 jwt
        filterChainDefinitionMap.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}
