package cn.edu.zjnu.learncs.interceptor;

import cn.edu.zjnu.learncs.NotFoundException;
import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.service.ContestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
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
        if (contest.isStarted())
            return true;
        else throw new NotFoundException();
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
