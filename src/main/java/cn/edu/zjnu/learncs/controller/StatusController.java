package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import cn.edu.zjnu.learncs.service.ProblemService;
import cn.edu.zjnu.learncs.service.SolutionService;
import cn.edu.zjnu.learncs.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/status")
public class StatusController {
    private static final int PAGE_SIZE = 30;

    @GetMapping
    public String showStatus() {
        return "problem/status";
    }
}

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/status")
class StatusAPIController {
    private final int PAGE_SIZE = 50;


    @Autowired
    SolutionService solutionService;
    @Autowired
    UserService userService;
    @Autowired
    ProblemService problemService;

    @GetMapping
    public Page<Solution> searchStatus(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                       @RequestParam(value = "user", defaultValue = "") String username,
                                       @RequestParam(value = "pid", defaultValue = "") Long pid,
                                       @RequestParam(value = "AC", defaultValue = "") String AC) {
        if (AC.equals("true"))
            AC = "Accepted";
        else
            AC = "";
        page = Math.max(page, 0);
        boolean getAll = false;
        if (username.equals("") && pid == null && AC.equals(""))
            getAll = true;
        User user = userService.getUserByUsername(username);
        Problem problem = problemService.getActiveProblemById(pid);
        return getAll ?
                solutionService.getStatus(page, PAGE_SIZE) :
                solutionService.getStatus(user, problem, AC, page, PAGE_SIZE);
    }
}
