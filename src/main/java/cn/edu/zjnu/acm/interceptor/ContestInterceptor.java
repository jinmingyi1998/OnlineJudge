package cn.edu.zjnu.acm.interceptor;

import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ContestService;
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
public class ContestInterceptor implements HandlerInterceptor {
    @Autowired
    ContestService contestService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("currentUser") == null)
            throw new NeedLoginException();
        String url = request.getRequestURL().toString();
        String[] sp = url.split("/");
        if (sp.length <= 3) return true;
        long cid;
        try {
            cid = Long.parseLong(sp[sp.length - 1]);
        } catch (NumberFormatException e) {
            throw new NotFoundException();
        }
        Contest contest = contestService.getContestById(cid, false);
        if (contest != null && contest.isStarted() && session.getAttribute("contest" + contest.getId()) != null)
            return true;
        response.setStatus(404);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

    }
}
