package cn.edu.zjnu.acm.config;

import cn.edu.zjnu.acm.interceptor.*;
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
    LoginViewInterceptor getLoginViewInterceptor() {
        return new LoginViewInterceptor();
    }

    @Bean
    AdminCheckInterceptor getAdminCheckInterceptor() {
        return new AdminCheckInterceptor();
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

    @Bean
    UnavailableInterceptor getUnavailableInterceptor() {
        return new UnavailableInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getUnavailableInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/judge/callback");
        String[] needLogin = {"/contest/**", "/team/**", "/problems/**", "/user/**", "/admin/**", "/status/**", "/forum/**"};
        registry.addInterceptor(getLoginViewInterceptor())
                .addPathPatterns(needLogin);
        registry.addInterceptor(getTeacherCheckInterceptor())
                .addPathPatterns("/admin/**")
                .addPathPatterns("/contest/create/0")
                .addPathPatterns("/api/admin/**");
        registry.addInterceptor(getAdminCheckInterceptor())
                .addPathPatterns("/admin/settings")
                .addPathPatterns("/api/admin/config")
                .addPathPatterns("/api/admin/correctData");
        registry.addInterceptor(getContestInterceptor())
                .addPathPatterns("/contest/*/**")
                .excludePathPatterns("/contest/*")
                .excludePathPatterns("/contest/problem/*")
                .excludePathPatterns("/contest/create/**")
                .excludePathPatterns("/contest/edit/**")
                .addPathPatterns("/api/contest/*/**")
                .excludePathPatterns("/api/contest/gate/*")
                .excludePathPatterns("/api/contest/*")
                .excludePathPatterns("/api/contest/background/**");
        String[] apiNeedLogin = {
                "/api/problems/submit/*",
                "/api/problems/analysis/**",
                "/api/status/view/*",
                "/api/status/share/*",
                "/api/user/edit/*",
                "/api/team/**",
                "/api/forum/**"};
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns(apiNeedLogin)
                .addPathPatterns();
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
