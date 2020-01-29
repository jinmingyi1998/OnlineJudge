package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.NotFoundException;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import cn.edu.zjnu.learncs.service.RESTService;
import cn.edu.zjnu.learncs.service.SolutionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Slf4j
@CrossOrigin
@RestController
public class MainController {
    @Autowired
    private RESTService restService;

    @Autowired
    private SolutionService solutionService;

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView m = new ModelAndView("index");
//        String res = restService.postJson("", "http://www.baidu.com");
//        System.out.println(res);
        return m;
    }

    @Data
    static class JudgeCallback {
        @NotNull
        private Long submit_id;
        private String err;
        private String info;
        private ArrayList<RunMessage> results;


        @Data
        static class RunMessage {
            public static final String[] code = new String[]{Solution.AC,
                    Solution.TLE,
                    Solution.TLE,
                    Solution.MLE,
                    Solution.RE,
                    Solution.SE};
            private int cpu_time;
            private int real_time;
            private int memory;
            private int signal;
            private int exit_code;
            private int error;
            private int result;
        }
    }

    @PostMapping("/judge/callback")
    public String judgeCallback(@RequestBody JudgeCallback callback) {
        try {
            log.info(callback.toString());
            Solution solution = solutionService.getSolutionById(callback.getSubmit_id());
            if (solution == null) {
                return "no this id";
            }
            if (callback.getErr() != null) {
                if (callback.getErr().equals("CE")) {
                    solution.setResult(Solution.CE);
                } else {
                    solution.setResult(Solution.SE);
                }
                solution.setInfo(callback.getInfo());
                solutionService.updateSolutionResultInfo(solution);
                return "success";
            } else if (callback.getResults().size() == 0) {
                solution.setResult(Solution.SE);
                solution.setInfo("No results");
                solutionService.updateSolutionResultInfo(solution);
                return "success";
            }
            int cpu = 0;
            int memory = 0;
            int caseNumber = 0;
            for (int i = 0; i < callback.getResults().size(); i++) {
                caseNumber = i + 1;
                JudgeCallback.RunMessage runMessage = callback.getResults().get(i);
                solution.setResult(JudgeCallback.RunMessage.code[runMessage.getResult()]);
                if (runMessage.getResult() > 3) {
                    cpu = memory = 0;
                    break;
                }
                cpu = Math.max(cpu, runMessage.getCpu_time());
                memory = Math.max(memory, runMessage.getMemory());
            }
            solution.setCaseNumber(caseNumber);
            solution.setTime(cpu);
            solution.setMemory(memory);
            solutionService.updateSolutionResultTimeMemory(solution);
            return "success";
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }
//    @GetMapping
//    public String baidu(){
//        String res = restService.postJson("{\"userName\":\"test\",\"password\":\"123456\"}", "http://10.65.163.65:5050/code_artisan_war/home/login");
//        return res;
//    }

}
