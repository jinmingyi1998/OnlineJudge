package cn.edu.zjnu.acm.config;

import cn.edu.zjnu.acm.interceptor.ContestInterceptor;
import cn.edu.zjnu.acm.interceptor.LoginInterceptor;
import cn.edu.zjnu.acm.interceptor.TeacherCheckInterceptor;
import cn.edu.zjnu.acm.interceptor.TeamInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        registry.addInterceptor(getTeacherCheckInterceptor())
                .addPathPatterns("/admin/**")
                .addPathPatterns("/contest/create/0");
        registry.addInterceptor(getContestInterceptor())
                .addPathPatterns("/contest/*/**")
                .excludePathPatterns("/contest/*")
                .excludePathPatterns("/contest/problem/*")
                .excludePathPatterns("/contest/create/**")
                .addPathPatterns("/api/contest/*/**")
                .excludePathPatterns("/api/contest/gate/*")
                .excludePathPatterns("/api/contest/*");
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/team/*")
                .addPathPatterns("/api/status/view/**");
        registry.addInterceptor(getTeamInterceptor())
                .addPathPatterns("/team/manage/**")
                .addPathPatterns("/contest/create/*")
                .excludePathPatterns("/contest/create/0")
                .addPathPatterns("/api/team/**")
                .excludePathPatterns("/api/team")
                .excludePathPatterns("/api/team/*")
                .excludePathPatterns("/api/team/apply/*")
                .excludePathPatterns("/api/team/invite/**");
    }
}
