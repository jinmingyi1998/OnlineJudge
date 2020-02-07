package cn.edu.zjnu.acm.config;

import cn.edu.zjnu.acm.interceptor.ContestInterceptor;
import cn.edu.zjnu.acm.interceptor.LoginInterceptor;
import cn.edu.zjnu.acm.interceptor.TeacherCheckInterceptor;
import cn.edu.zjnu.acm.interceptor.TeamInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    @Bean
    TeacherCheckInterceptor getTeacherCheckInterceptor() {
        return new TeacherCheckInterceptor();
    }

    @Bean
    ContestInterceptor getContestInterceptor() {
        return new ContestInterceptor();
    }

    @Bean
    LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    TeamInterceptor getTeamInterceptor() {
        return new TeamInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> stringList = new ArrayList<>();
        stringList.add("/oj/admin/**");
        registry.addInterceptor(getTeacherCheckInterceptor())
                .addPathPatterns(stringList);
        registry.addInterceptor(getContestInterceptor())
                .addPathPatterns("/contest/*/**")
                .excludePathPatterns("/contest/*")
                .excludePathPatterns("/contest/problem/*")
                .addPathPatterns("/api/contest/*/**")
                .excludePathPatterns("/api/contest/gate/*")
                .excludePathPatterns("/api/contest/*");
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/team/*")
                .addPathPatterns("/api/status/view/**");
        registry.addInterceptor(getTeamInterceptor())
                .addPathPatterns("/team/manage/**")
                .addPathPatterns("/api/team/**")
                .excludePathPatterns("/api/team")
                .excludePathPatterns("/api/team/*")
                .excludePathPatterns("/api/team/apply/*")
                .excludePathPatterns("/api/team/invite/**");
    }
}
