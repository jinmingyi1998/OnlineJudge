package cn.edu.zjnu.learncs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MainController {
    @GetMapping
    public ModelAndView home(){
        ModelAndView m = new ModelAndView("index");
        return m;
    }
}
