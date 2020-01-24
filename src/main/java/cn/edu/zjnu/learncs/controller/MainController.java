package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.service.RESTService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Slf4j
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

    @Data
    static class JudgeCallback {
        @Data
        static class RunMessage {
            private Long cpu_time;
            private Long real_time;
            private Long memory;
            private Long signal;
            private Long exit_code;
            private Long error;
            private Long result;

            @Override
            public String toString() {
                return "RunMessage{" +
                        "cpu_time=" + cpu_time +
                        ", real_time=" + real_time +
                        ", memory=" + memory +
                        ", signal=" + signal +
                        ", exit_code=" + exit_code +
                        ", error=" + error +
                        ", result=" + result +
                        '}';
            }
        }

        private ArrayList<RunMessage> results;

        @Override
        public String toString() {
            return "JudgeCallback{" +
                    "results=" + results +
                    '}';
        }
    }

    @PostMapping("/judge/callback")
    public String judgeCallback(@RequestBody JudgeCallback callback) {
        log.info(callback.toString());
        return "success";
    }
//    @GetMapping
//    public String baidu(){
//        String res = restService.postJson("{\"userName\":\"test\",\"password\":\"123456\"}", "http://10.65.163.65:5050/code_artisan_war/home/login");
//        return res;
//    }

}
