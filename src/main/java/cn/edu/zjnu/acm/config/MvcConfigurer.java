package cn.edu.zjnu.acm.config;

import cn.edu.zjnu.acm.interceptor.ContestInterceptor;
import cn.edu.zjnu.acm.interceptor.SessionHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {

    @Bean
    public ContestInterceptor getContestInterceptor(){
        return new ContestInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> stringList = new ArrayList<>();
        stringList.add("/oj/admin/**");
        registry.addInterceptor(new SessionHandlerInterceptor()).addPathPatterns(stringList);
        registry.addInterceptor(getContestInterceptor()).addPathPatterns("/contest/*/**").excludePathPatterns("/contest/*")
                .addPathPatterns("/api/contest/*/**").excludePathPatterns("/api/contest/*");
    }
}
