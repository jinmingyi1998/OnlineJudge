package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequestMapping("/problems")
@RestController
public class ProblemController {
    private static final int PAGE_SIZE = 30;
    @Autowired
    ProblemService problemService;

    @GetMapping
    public ModelAndView showProblemList(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "problem", defaultValue = "") String search) {
        ModelAndView m = new ModelAndView("problem/problemlist");
        page = Math.max(page, 0);
        Page<Problem> problemPage;
        if (search != null && search.length() > 0) {
            problemPage = problemService.searchActiveProblem(page, PAGE_SIZE, search);
        } else {
            problemPage = problemService.getAllActiveProblems(page, PAGE_SIZE);
        }
        m.addObject("problems", problemPage);
//        m.addObject("tags", tagRepository.findAll());
        return m;
    }

}
