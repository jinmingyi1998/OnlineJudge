package cn.edu.zjnu.acm.interceptor;

import cn.edu.zjnu.acm.entity.Teacher;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.repo.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@ComponentScan
public class TeacherCheckInterceptor implements HandlerInterceptor {
    @Autowired
    TeacherRepository teacherRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                log.info(String.format("admin page intercepted:%s %s at %s", user.getId() + "", user.getUsername(), request.getRemoteAddr()));
                Teacher teacher = teacherRepository.findByUser(user).orElse(null);
                if (teacher != null)
                    return true;
            }
            response.setStatus(403);
            return false;
        } catch (Exception e) {
            response.setStatus(404);
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
