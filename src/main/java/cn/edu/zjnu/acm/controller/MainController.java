package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.service.JudgeService;
import cn.edu.zjnu.acm.service.SolutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@CrossOrigin
@RestController
public class MainController {
    private final JudgeService judgeService;
    private final Config config;
    private final SolutionService solutionService;

    public MainController(JudgeService judgeService, Config config, SolutionService solutionService) {
        this.judgeService = judgeService;
        this.config = config;
        this.solutionService = solutionService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView m = new ModelAndView("index");
        return m;
    }

    @GetMapping("/notice")
    public String getNotice() {
        return config.getNotice();
    }
}
