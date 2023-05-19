package com.dz.config;


import com.dz.interceptor.jwtValidateinterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private jwtValidateinterceptor jwtValidateinterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    InterceptorRegistration registration  =  registry.addInterceptor(jwtValidateinterceptor);
        registration.addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/info",
                        "/user/logout",
                        "/error",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/**");
    }
}
