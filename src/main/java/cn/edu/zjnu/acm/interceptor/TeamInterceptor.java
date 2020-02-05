package cn.edu.zjnu.acm.interceptor;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ComponentScan
public class TeamInterceptor implements HandlerInterceptor {
    @Autowired
    TeamService teamService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null)
            return false;
        String url = String.valueOf(request.getRequestURL());
        String[] sp = url.split("/");
        try {
            Long tid = Long.parseLong(sp[sp.length - 1]);
            Team team = teamService.getTeamById(tid);
            if (teamService.getUserInTeam(user, team).getLevel() > Teammate.MANAGER)
                throw new ForbiddenException();
        } catch (NumberFormatException e) {
            return true;
        }catch (NullPointerException e){
            throw new NotFoundException();
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
