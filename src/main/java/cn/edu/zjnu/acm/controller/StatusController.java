package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ProblemService;
import cn.edu.zjnu.acm.service.SolutionService;
import cn.edu.zjnu.acm.service.UserService;
import cn.edu.zjnu.acm.util.RestfulResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/status")
class StatusViewController {
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
public class StatusController {
    private static final int PAGE_SIZE = 50;
    private final Config config;
    private final SolutionService solutionService;
    private final UserService userService;
    private final ProblemService problemService;
    private final HttpSession session;

    public StatusController(Config config, SolutionService solutionService, UserService userService, ProblemService problemService, HttpSession session) {
        this.config = config;
        this.solutionService = solutionService;
        this.userService = userService;
        this.problemService = problemService;
        this.session = session;
    }

    public static Solution solutionFilter(Solution s) {
        if (s.getContest() != null && !s.getContest().isEnded()) {
            s.getUser().setId(0l);
            s.getUser().setName("contest user");
            s.getUser().setUsername("contest user");
            s.setLanguage("c");
            s.setCaseNumber(0);
            s.getProblem().setId(0l);
            s.setTime(0);
            s.setMemory(0);
            s.setLength(0);
            s.setShare(false);
        }
        s.setIp(null);
        s.getUser().hideInfo();
        Problem p = Problem.jsonReturnProblemFactory();
        p.setId(s.getProblem().getId());
        s.setProblem(p);
        s.setContest(null);
        return s;
    }

    @GetMapping("")
    public RestfulResult searchStatus(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "user", defaultValue = "") String username,
                                      @RequestParam(value = "pid", defaultValue = "") Long pid,
                                      @RequestParam(value = "AC", defaultValue = "") String AC) throws Exception {
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
        Page<Solution> page_return = getAll ?
                solutionService.getStatus(page, PAGE_SIZE) :
                solutionService.getStatus(user, problem, AC, page, PAGE_SIZE);
        page_return.getContent().forEach(s -> {
            try {
                s.setUser(s.getUser().clone());
            } catch (CloneNotSupportedException ignored) {
            }
            s.setSource(null);
            s.setInfo(null);
            solutionFilter(s);
        });
        return new RestfulResult(200, "success", page_return);
    }

    @GetMapping("/view/{id:[0-9]+}")
    public Solution restfulShowSourceCode(@PathVariable(value = "id") Long id) {
        Solution solution = solutionService.getSolutionById(id);
        try {
            assert solution != null;
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                solution.setInfo(null);
                solution.setSource("This Source Code Is Not Shared!");
            }
            if (userService.getUserPermission(user) == -1) {
                if (user.getId() != solution.getUser().getId()) {
                    // This submit not belongs to this user.
                    solution.setSource("This Source Code Is Not Shared!");
                    solution.setInfo(null);
                } else if (user.getUserProfile().getScore() < config.getLeastScoreToSeeOthersCode() && solution.getShare()) {
                    // This submit is not shared and user doesn't have enough score
                    solution.setSource("This Source Code Is Not Shared!");
                    solution.setInfo(null);
                } else {
                    solution.setContest(null);
                }
            } else {
                solution.setContest(null);
            }
            return solutionFilter(solution);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @PostMapping("/share/{id:[0-9]+}")
    public boolean setShare(@PathVariable("id") Long id) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (userService.getUserById(user.getId()) != null) {
                Solution solution = solutionService.getSolutionById(id);
                if (solution.getUser().getId().equals(user.getId())) {
                    solution.setShare(!solution.getShare());
                    solutionService.updateSolutionShare(solution);
                    return solution.getShare();
                }
            }
        } catch (Exception ignored) {
        }
        throw new NotFoundException();
    }

    @GetMapping("/user/latest/submit/{id:[0-9]+}")
    public RestfulResult userSubmitLatestHistory(@PathVariable("id") Long pid) {
        User user = (User) session.getAttribute("currentUser");
        Problem problem = problemService.getActiveProblemById(pid);
        try {
            if (userService.getUserById(user.getId()) != null && problem != null) {
                List<Solution> solutions = solutionService.getProblemSubmitOfUser(user, problem);
                solutions = solutions.subList(0, Math.min(solutions.size(), 5));
                solutions.forEach(s -> {
                    s.setUser(null);
                    s.setProblem(null);
                    s.setIp(null);
                    s.setSource(null);
                    s.setContest(null);
                });
                return new RestfulResult(200, RestfulResult.SUCCESS, solutions);
            }
        } catch (Exception ignored) {
        }
        return new RestfulResult(200, RestfulResult.SUCCESS, new LinkedList<>());
    }

}
