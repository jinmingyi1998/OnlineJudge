package cn.edu.zjnu.acm.interceptor;

import cn.edu.zjnu.acm.config.GlobalStatus;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.repo.user.TeacherRepository;
import cn.edu.zjnu.acm.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UnavailableInterceptor implements HandlerInterceptor {
    @Autowired
    TeacherRepository teacherRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (GlobalStatus.maintaining) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(new Result(200,"维护中"));
            return false;
        }
        if (GlobalStatus.teacherOnly) {
            User user = (User) request.getSession().getAttribute("currentUser");
            if (user != null && teacherRepository.existsByUser(user)) {
                return true;
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(new Result(200,"维护中"));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
