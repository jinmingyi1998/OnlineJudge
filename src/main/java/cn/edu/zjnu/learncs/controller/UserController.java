package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.service.RESTService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    RESTService restService;
    private String userSystemURL = "";

    @GetMapping("/user/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        return modelAndView;
    }
    @GetMapping("/user/logout")
    public ModelAndView logout() {
        ModelAndView modelAndView = new ModelAndView("index.html");
        return modelAndView;
    }
    @GetMapping("/user/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("register.html");
        return modelAndView;
    }
    @PostMapping("/user/register")
    public User registerAction(User user, HttpSession session) {
        String jsonString = String.format("\"username\":\"%s\",\"password\":\"%s\"",user.getUsername(),user.getPassword());
        String result = restService.postJson(jsonString,userSystemURL+"/register");
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = jsonObject.getString("msg");
//        if(msg.equals("Request Success!"))
        return user;
    }

}
