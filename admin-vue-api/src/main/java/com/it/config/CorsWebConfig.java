package com.it.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsWebConfig {
//    @Bean
//    public FilterRegistrationBean corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration configuration = new CorsConfiguration();
//        /**
//         * *：允许所有域名跨域访问
//         * 我们可以指定放开某个域名跨域访问
//         */
//        configuration.addAllowedOrigin("http://localhost:5173/");
//        /**
//         * 允许所有请求头跨域访问
//         */
//        configuration.addAllowedHeader("*");
//        /**
//         * 允许所有请求方法跨域访问
//         */
//        configuration.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", configuration);
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CorsFilter(source));
//        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return filterRegistrationBean;
//    }
}
