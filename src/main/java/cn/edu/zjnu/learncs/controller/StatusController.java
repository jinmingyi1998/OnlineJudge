package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Solution;
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

    @GetMapping
    public Page<Solution> getStatus(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "user", defaultValue = "") String username,
                                    @RequestParam(value = "pid", defaultValue = "") Integer pid,
                                    @RequestParam(value = "AC", defaultValue = "") Integer AC) {
        page = Math.max(page, 0);
        Page<Solution> solutionPage;
        User user = userService.getUserByUsername(username);
        if (user == null && pid == null && AC == null)
            return solutionPage = solutionService.getStatus(page, PAGE_SIZE);

    }
}
