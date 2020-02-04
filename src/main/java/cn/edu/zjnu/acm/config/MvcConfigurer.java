package cn.edu.zjnu.acm.config;

import cn.edu.zjnu.acm.interceptor.ContestInterceptor;
import cn.edu.zjnu.acm.interceptor.LoginInterceptor;
import cn.edu.zjnu.acm.interceptor.TeacherCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> stringList = new ArrayList<>();
        stringList.add("/oj/admin/**");
        registry.addInterceptor(new TeacherCheckInterceptor())
                .addPathPatterns(stringList);
        registry.addInterceptor(new ContestInterceptor())
                .addPathPatterns("/contest/*/**")
                .excludePathPatterns("/contest/*")
                .excludePathPatterns("/contest/problem/*")
                .addPathPatterns("/api/contest/*/**")
                .excludePathPatterns("/api/contest/gate/*")
                .excludePathPatterns("/api/contest/*");
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/team/**")
                .addPathPatterns("/api/status/view/**");
    }
}
