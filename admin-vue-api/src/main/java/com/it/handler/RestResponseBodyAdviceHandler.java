package com.it.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.util.DataResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

@RestControllerAdvice
@Slf4j
public class RestResponseBodyAdviceHandler implements ResponseBodyAdvice<Object> {
    @Resource
    private ObjectMapper objectMapper;
    private final String stringConverter = "org.springframework.http.converter.StringHttpMessageConverter";

    /**
     * true:代表支持我们在响应前端的时候做一些处理(调用beforeBodyWrite方法)
     * false:不支持
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String name = returnType.getDeclaringClass().getName();
        log.info("supports:{}", name);
        /**
         * 排除 swagger-ui、静态图片、Spring Boot 错误页，避免包装非 JSON 或误把错误包成成功
         */
        if (name.contains("springfox")) {
            return false;
        }
        if (name.contains("BasicErrorController")) {
            return false;
        }
        if (name.endsWith("ImageController")) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        /**
         * 当接口返回到类型消息转换器是StringHttpMessageConverter
         * 我们才需要把它转换成string
         */
        if (stringConverter.equalsIgnoreCase(selectedConverterType.getName())) {
            HttpHeaders headers = response.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(DataResult.success(body));
        }
        if (body instanceof DataResult) {
            return body;
        }
        return DataResult.success(body);
    }
}