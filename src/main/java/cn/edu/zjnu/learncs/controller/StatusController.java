package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.NotFoundException;
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
    private final int PAGE_SIZE = 50;


    @Autowired
    SolutionService solutionService;
    @Autowired
    UserService userService;
    @Autowired
    ProblemService problemService;
    @Autowired
    HttpSession session;

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

    @GetMapping("/view/{id}")
    public Solution restfulShowSourceCode(@PathVariable(value = "id") Long id) {
        Solution solution = solutionService.getSolutionById(id);
        try {
//            User user = (User) session.getAttribute("currentUser");
            if (solution != null && solution.getShare()) {
                return solution;
            }
//            if (user != null && user.getId() == solution.getUser().getId()) {
//                return solution;
//            }
            solution.setSource("This Source Code Is Not Shared!");
            return solution;
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @PostMapping("/share/{id}")
    public boolean setShare(@PathVariable("id") Long id) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (userService.getUserById(user.getId()) != null) {
                Solution solution = solutionService.getSolutionById(id);
                if (solution.getUser().equals(user)) {
                    solution.setShare(!solution.getShare());
                    solutionService.updateSolution(solution);
                    return solution.getShare();
                }
            }
        } catch (Exception e) {
        }
        throw new NotFoundException();
    }

    @GetMapping("/user/latest/submit/{id}")
    public List<Solution> userSubmitLatestHistory(@PathVariable("id") Long pid) {
        User user = (User) session.getAttribute("currentUser");
        Problem problem = problemService.getActiveProblemById(pid);
        try {
            if (userService.getUserById(user.getId()) != null && problem != null) {
                List<Solution> solutions = solutionService.getProblemSubmitOfUser(user, problem);
//                List<Solution>solutions = solutionService.getStatus(0,50).getContent();
                return solutions.subList(0, Math.min(solutions.size(), 5));
            }
        } catch (Exception e) {
        }
        return new LinkedList<Solution>();
    }

}
