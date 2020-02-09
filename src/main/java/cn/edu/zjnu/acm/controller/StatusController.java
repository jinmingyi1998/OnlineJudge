package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ProblemService;
import cn.edu.zjnu.acm.service.SolutionService;
import cn.edu.zjnu.acm.service.UserService;
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
            s.setInfo("");
            s.setShare(false);
        }
        s.setIp(null);
        s.getUser().setPassword(null);
        s.getUser().setEmail(null);
        s.getUser().setIntro(null);
        s.getUser().setUserProfile(null);
        Problem p = Problem.jsonReturnProblemFactory();
        p.setId(s.getProblem().getId());
        s.setProblem(p);
        s.setContest(null);
        return s;
    }

    @GetMapping
    public Page searchStatus(@RequestParam(value = "page", defaultValue = "0") Integer page,
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
        for (Solution s : page_return.getContent()) {
            s.setUser(s.getUser().clone());
            s.setSource(null);
            s.setInfo(null);
            s = solutionFilter(s);
        }
        return page_return;
    }

    @GetMapping("/view/{id:[0-9]+}")
    public Solution restfulShowSourceCode(@PathVariable(value = "id") Long id) {
        Solution solution = solutionService.getSolutionById(id);
        try {
            assert solution != null;
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                if (user.getId() == solution.getUser().getId()) {
                    // This submit belongs to this user.
                    return solutionFilter(solution);
                }
                if (user.getUserProfile().getScore() > config.getLeastScoreToSeeOthersCode() && solution.getShare()) {
                    // This submit is shared
                    return solutionFilter(solution);
                }
            }
            solution.setSource("This Source Code Is Not Shared!");
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
        } catch (Exception e) {
        }
        throw new NotFoundException();
    }

    @GetMapping("/user/latest/submit/{id:[0-9]+}")
    public List<Solution> userSubmitLatestHistory(@PathVariable("id") Long pid) {
        User user = (User) session.getAttribute("currentUser");
        Problem problem = problemService.getActiveProblemById(pid);
        try {
            if (userService.getUserById(user.getId()) != null && problem != null) {
                List<Solution> solutions = solutionService.getProblemSubmitOfUser(user, problem);
//                List<Solution>solutions = solutionService.getStatus(0,50).getContent();
                solutions = solutions.subList(0, Math.min(solutions.size(), 5));
                for (Solution s : solutions) {
                    s.setUser(null);
                    s.setProblem(null);
                    s.setIp(null);
                    s.setSource(null);
                    s.setContest(null);
                }
                return solutions;
            }
        } catch (Exception e) {
        }
        return new LinkedList<Solution>();
    }

}
