package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.service.RESTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
public class MainController {
    @Autowired
    private RESTService restService;
    @GetMapping("/")
    public ModelAndView home(){
        ModelAndView m = new ModelAndView("index");
//        String res = restService.postJson("", "http://www.baidu.com");
//        System.out.println(res);
        return m;
    }
//    @GetMapping
//    public String baidu(){
//        String res = restService.postJson("{\"userName\":\"test\",\"password\":\"123456\"}", "http://10.65.163.65:5050/code_artisan_war/home/login");
//        return res;
//    }

}
