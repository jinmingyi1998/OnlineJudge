package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.NotFoundException;
import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.*;
import cn.edu.zjnu.learncs.repo.ContestProblemRepository;
import cn.edu.zjnu.learncs.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/oj/contest")
class ContestViewController {
    @GetMapping
    public String contestPage(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "title", defaultValue = "") String title) {
//        ModelAndView m = new ModelAndView("contest/contests");
        return "contest/contests";
//        page = Math.max(0, page);
//        Page<Contest> contests = contestService.getContestPage(page, title);
//        m.addObject("contests", contests);
//        return m;
    }

    @RequestMapping(value = "/{id}")
    public String showContest(@PathVariable(value = "id") Long id) {
        return "contest/contestinfo";
    }


//    @GetMapping("/api/rank/{cid}")
//    public Rank getRankOfContest(@PathVariable Long cid) {
//        try {
//            @NotNull Contest contest = contestService.getContestById(cid);
//            @NotNull Rank rank = new Rank(contest);
//            @NotNull List<Solution> solutions = solutionService.getSolutionsInContest(contest);
//            rank.init(solutions);
//            return rank;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        throw new NotFoundException();
//    }


//    @GetMapping("/background/{cid}")
//    public ModelAndView backgroundOfContest(@PathVariable Long cid) {
//        try {
//            ModelAndView m = new ModelAndView("contest/background");
//            Contest contest = contestService.getContestById(cid);
//            m.addObject("contest", contest);
//            return m;
//        } catch (Exception e) {
//        }
//        throw new NotFoundException();
//    }
}

@Slf4j
@RestController
@RequestMapping("/api/contest")
public class ContestController {
    @Autowired
    HttpSession session;
    @Autowired
    UserService userService;
    @Autowired
    ProblemService problemService;
    @Autowired
    SolutionService solutionService;
    @Autowired
    ContestService contestService;
    @Autowired
    RESTService restService;
    @Autowired
    ContestProblemRepository contestProblemRepository;
    private static final int PAGE_SIZE = 30;

    @PostMapping("/{cid}/submit/{pid}")
    public String submitProblemInContest(@PathVariable("pid") Long pid,
                                         @PathVariable("cid") Long cid,
                                         HttpServletRequest request, @RequestBody ProblemController.SubmitCodeObject submitCodeObject) {

        log.info("Submit:" + Date.from(Instant.now()));

        String source = submitCodeObject.getSource();
        boolean share = submitCodeObject.isShare();
        String language = submitCodeObject.getLanguage();
        String _temp = ProblemController.checkSubmitFrequncy(session, source);
        if (_temp != null)
            return _temp;
        @NotNull User user;
        try {
            user = (User) session.getAttribute("currentUser");
            if (userService.getUserById(user.getId()) == null) {// user doesn't login
                log.debug("User not exist");
                return "Please Login";
            }
        } catch (Exception e) {
            return "Please Login";
        }
        try {
            Contest contest = contestService.getContestById(cid);
            if (contest.isEnded())
                return "The contest has ended!";
            Problem problem = contestProblemRepository.findByContestAndTempId(contest, pid).get().getProblem();
            if (problem == null) {
                return "Problem Not Exist";
            }
            Solution solution = new Solution(user, problem, language, source, request.getRemoteAddr(), share);
            solution = solutionService.insertSolution(solution);
            restService.submitCode(solution);
            return "success";
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @PostMapping("/{cid}/comments/post")
    public String postComments(@RequestParam("post_comment") String text, @PathVariable(value = "cid") Long cid) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            User user = (User) session.getAttribute("currentUser");
            Comment comment = new Comment(user, text, contest);
            comment = contestService.postComment(comment);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }

    @GetMapping("{cid}/comments")
    public List<Comment> getCommentsOfContest(@PathVariable Long cid) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            List<Comment> contestComments = contestService.getCommentsOfContest(contest);
            return contestComments;
        } catch (Exception e) {
        }
        return null;
    }

    @PostMapping("/{cid}/view/{id}")
    public Solution restfulShowSourceCodeInContest(@PathVariable(value = "cid") Long cid, @PathVariable(value = "id") Long id) {
        try {
            @NotNull Solution solution = solutionService.getSolutionById(id);
            @NotNull Contest contest = contestService.getContestById(cid);
            User user = (User) session.getAttribute("currentUser");
            if (user != null && Objects.equals(user.getId(), solution.getUser().getId())) {
                return solution;
            }
            if (contest.isEnded() && solution.getShare()) {
                return solution;
            }
        } catch (Exception e) {
        }
        throw new NotFoundException();
    }

    @GetMapping("/status/{cid}")
    public Page<Solution> getUserSolutions(@PathVariable("cid") Long cid, @Param(value = "page") int page) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            @NotNull User user = (User) session.getAttribute("currentUser");
            @NotNull Page<Solution> solutions = solutionService.getSolutionsOfUserInContest(page, PAGE_SIZE, user, contest);
            for (Solution s : solutions.getContent()) {
                for (ContestProblem cp : contest.getProblems()) {
                    if (cp.getProblem().getId() == s.getProblem().getId()) {
                        s.getProblem().setId(cp.getTempId());
                        break;
                    }
                }
            }
            return solutions;
        } catch (Exception e) {
        }
        throw new NotFoundException();
    }

    @GetMapping("/{cid}/problem/{pid}")
    public Problem getProblemByContestTempId(@PathVariable(value = "cid") Long cid,
                                             @PathVariable("pid") Long pid) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            @NotNull Problem problem = contestProblemRepository.findByContestAndTempId(contest, pid).get().getProblem();
            assert problem != null;
            return problem;
        } catch (Exception e) {
        }
        throw new NotFoundException();
    }

    public Boolean checkPassword(Contest contest, String password) {
        if (contest.getPassword().equals(password)) {
            session.setAttribute("contest" + contest.getId(), contest);
            return true;
        }
        return false;
    }
    /*@PostMapping("/background/edit/{cid}")
    public String insertContestAction(@PathVariable Long cid,
                                      @RequestParam(value = "title") String title,
                                      @RequestParam(value = "description") String description,
                                      @RequestParam(value = "privilege", defaultValue = "public") String privilege,
                                      @RequestParam(value = "password", defaultValue = "") String password,
                                      @RequestParam(value = "startTime") String startTime,
                                      @RequestParam(value = "lastTime") String lastTime,
                                      @RequestParam(value = "list[]") String[] ids) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            contest.setTitle(title);
            contest.setCreateTime(Instant.now());
            contest.setDescription(description);
            contest.setPrivilege(privilege);
            contest.setStartTime(startTime);
            contest.setEndTime(startTime, lastTime);
            contest.setPassword(password);
            List<ContestProblem> contestProblems = new ArrayList<>();
            Long _cnt = 1L;
            for (int i = 0; i < ids.length; i++) {
                String[] _s = ids[i].split("&", 2);
                Long id = Long.parseLong(_s[0]);
                Problem p = problemService.getProblemById(id);
                if (p != null) {
                    contestProblems.add(new ContestProblem(p, _s[1], _cnt++));
                }
            }
            for (ContestProblem cp : contestProblems) {
                cp.setContest(contest);
                contestProblemRepository.save(cp);
            }
            return "success";
        } catch (Exception e) {
        }
        return null;
    }*/
  /*  @GetMapping("/rejudge/{cid}")
    public String Rejudge(@PathVariable Long cid) {
        Contest contest = contestService.getContestById(cid);
        RejudgeThread rejudgeThread = new RejudgeThread();
        List<Solution> solutions = solutionService.getSolutionsInContest(contest);
        if (!contest.getPattern().equals("acm")) {
            solutions.sort((o1, o2) -> (int) (o2.getId() - o1.getId()));
        }
        rejudgeThread.solutions = solutions;
        rejudgeThread.run();
        return "Running";
    }*/
}