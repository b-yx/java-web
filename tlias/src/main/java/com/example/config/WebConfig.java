package com.example.config;

import com.example.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)           // 注册拦截器
                .addPathPatterns("/**")                    // 拦截所有请求
                .excludePathPatterns("/login");            // 排除登录接口（其实已经在里面判断了，这里再排除更保险）
    }
}
