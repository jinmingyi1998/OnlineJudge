package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.NotFoundException;
import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;

@Slf4j
@RequestMapping("/problems")
@Controller
public class ProblemController {
    @GetMapping
    public String problemsList() {
        return "problem/problemlist";
    }

    @GetMapping("/{id}")
    public String showproblem(@PathVariable Long id) {
        return "problem/showproblem";
//        Problem problem = problemService.getProblemById(id);
//        ModelAndView m = new ModelAndView("problem/showproblem");
//        if (problem == null || problem.getActive() == false) {
//            throw new NotFoundException();
//        } else
//            m.addObject("problem", problemService.getProblemById(id));
//        List<Solution> solutionList = new ArrayList<>();
//        if (session.getAttribute("currentUser") != null) {
//            User user = (User) session.getAttribute("currentUser");
//            solutionList = solutionService.getTop5ProblemSolution(user, problem);
//        }
//        m.addObject("solutions", solutionList);
//        return m;
    }

}


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/problems")
class ProblemAPIController {
    private static final int PAGE_SIZE = 30;
    @Autowired
    ProblemService problemService;
    @Autowired
    private HttpSession session;

    static class SubmitCodeObject {
        public SubmitCodeObject() {
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getSource() {
            return source;
        }


        public String getLanguage() {
            return language;
        }

        @Override
        public String toString() {
            return "SubmitCodeObject{" +
                    "source='" + source + '\'' +
                    ", language='" + language + '\'' +
                    '}';
        }

        private String source;
        private String language;
    }

    @GetMapping
    public Page<Problem> showProblemList(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "search", defaultValue = "") String search) {
        page = Math.max(page, 0);
        Page<Problem> problemPage;
        if (search != null && search.length() > 0) {
            problemPage = problemService.searchActiveProblem(page, PAGE_SIZE, search);
        } else {
            problemPage = problemService.getAllActiveProblems(page, PAGE_SIZE);
        }
        return problemPage;
    }

    @GetMapping("/{id}")
    public Problem showProblem(@PathVariable Long id) {
        Problem problem = problemService.getActiveProblemById(id);
        if (problem == null)
            throw new NotFoundException();
        return problem;
    }

    @PostMapping("/submit/{id}")
    public String submitProblem(@PathVariable("id") Long id,
                                @RequestBody SubmitCodeObject submitCodeObject,
                                HttpServletRequest request
    ) {
        String source = submitCodeObject.getSource();
        String language = submitCodeObject.getLanguage();
        if (session.getAttribute("last_submit") != null) {
            Instant instant = (Instant) session.getAttribute("last_submit");
            if (Instant.now().minusSeconds(10).compareTo(instant) < 0) {
                return "Don't submit within 10 seconds";
            } else if (source.length() > 20000) {
                return "Source code too long";
            } else if (source.length() < 2) {
                return "Source code too short";
            }
        }
        session.setAttribute("last_submit", Instant.now());
        try {
            User user = (User) session.getAttribute("currentUser");
//            if (!userAuthorityService.isLogin(user)) {// user doesn't login
//                log.error("User not exist");
//                return "Please Login";
//            }
//            Problem problem = problemService.getProblemById(id);
//            if (problem == null || problem.getActive() == false) {
//                return "Problem Not Exist";
//            }
            //null检验完成

//            Solution solution = new Solution(user, problem, language, source, request.getRemoteAddr(), share);

//            judgeService.submit(solution);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }
}
