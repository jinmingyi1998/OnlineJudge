package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.time.Instant;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("user/register");
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        try {
            User t_user = userService.registerUser(user);
            if (t_user != null)
                return "success";
            else return "用户名已存在 user already existed";
        } catch (ConstraintViolationException e) {
            return "format error 格式错误";
        }
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.invalidate();
        return login();
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user, HttpSession session, Model m) {
        User login_user = userService.loginUser(user);
        if (login_user == null ) {
            return "用户名或密码错误。";
        }
        session.setMaxInactiveInterval(6 * 60 * 60);
        session.setAttribute("currentUser", login_user);
        session.setAttribute("loginTime", Instant.now());
        return "success";
    }

    @Autowired
    HttpSession session;
    @GetMapping("/user/session")
    public User getSession(){
        User user = (User) session.getAttribute("currentUser");
        if(user!=null)
            return user.hideInfo();
        throw new NeedLoginException();
    }
}
