package com.it.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/**
 * 〈文件上传配置〉<br>
 * 图片访问由 {@link com.it.controller.ImageController} 提供（含缺失文件回退 default.jpg）。
 *
 * @author
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * 文件上传配置
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(DataSize.parse("1048576KB")); // KB,MB
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.parse("1048576KB"));
        return factory.createMultipartConfig();
    }

}