package cn.edu.zjnu.learncs.controller;

import cn.edu.zjnu.learncs.NotFoundException;
import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.*;
import cn.edu.zjnu.learncs.repo.ContestProblemRepository;
import cn.edu.zjnu.learncs.service.*;
import cn.edu.zjnu.learncs.util.Rank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/contest")
class ContestViewController {
    @GetMapping
    public String contestPage() {
        return "contest/contests";
    }

    @GetMapping("/{id}")
    public String showContest(@PathVariable(value = "id") Long id) {
        return "contest/contestinfo";
    }

    @GetMapping("/status/{id}")
    public String showContestStatus(@PathVariable(value = "id") Long id) {
        return "contest/conteststatus";
    }

    @GetMapping("/ranklist/{id}")
    public String showContestRanklist(@PathVariable(value = "id")Long id){
        return "contest/contestrank";
    }


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
@CrossOrigin
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

    @GetMapping
    public Page<Contest> showContests(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "search", defaultValue = "") String search) {
        Page<Contest> currentPage = contestService.getContestPage(page, PAGE_SIZE, search);
        for (Contest c : currentPage.getContent()) {
            c.setProblems(null);
            c.setSolutions(null);
            c.setContestComments(null);
            c.setPassword(null);
            c.setCreator(null);
            c.setFreezeRank(null);
            c.setCreateTime(null);
        }
        return currentPage;
    }

    @GetMapping("/{cid}")
    public Contest getContestDetail(@PathVariable("cid") Long cid,
                                    @RequestParam(value = "password", defaultValue = "") String password) {
        Contest c = contestService.getContestById(cid, false);
        if (c == null)
            throw new NotFoundException();
        Contest scontest = (Contest) session.getAttribute("contest" + c.getId());
        if (scontest == null || scontest.getId() != c.getId()) {
            if (c.getPassword().length() > 0 &&
                    c.getPrivilege().equals("private") &&
                    !c.getPassword().equals(password)) {
                c.setProblems(null);
                c.setSolutions(null);
                c.setContestComments(null);
                c.setCreator(null);
                c.setFreezeRank(null);
                c.setCreateTime(null);
                c.setPassword("password");
                return c;
            } else {
                session.setAttribute("contest" + c.getId(), c);
            }
        }
        try {
            c = contestService.getContestById(cid, true);
            c.setSolutions(null);
            c.setCreator(null);
            c.setCreateTime(null);
            c.setPassword(null);
            c.setFreezeRank(null);
            c.setCreateTime(null);
            for (ContestProblem cp : c.getProblems()) {
                Problem p = cp.getProblem();
                p.setId(null);
                p.setAccepted(null);
                p.setSubmit(null);
                p.setAccepted(null);
                p.setTags(null);
                p.setTitle(null);
                p.setScore(null);
                p.setSubmit(null);
                p.setSource(null);
            }
        } catch (Exception e) {
            throw new NotFoundException();
        }
        return c;
    }

    @PostMapping("/{cid}/submit/{pid}")
    public String submitProblemInContest(@PathVariable("pid") Long pid,
                                         @PathVariable("cid") Long cid,
                                         HttpServletRequest request,
                                         @RequestBody ProblemController.SubmitCodeObject submitCodeObject) {
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
            Contest scontest = (Contest) session.getAttribute("contest" + cid);
            if (scontest == null || scontest.getId() != contest.getId()) {
                return "Need attendance!";
            }
            if (contest.isEnded()) {
                return "The contest id ended!";
            }
            ContestProblem cproblem = contestProblemRepository.findByContestAndTempId(contest, pid).orElse(null);
            if (cproblem == null) {
                return "Problem Not Exist";
            }
            Problem problem = cproblem.getProblem();
            Solution solution = new Solution(user, problem, language, source, request.getRemoteAddr(), share);
            solution.setContest(contest);
            solution = solutionService.insertSolution(solution);
            assert solution.getContest() != null;
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
    public Solution restfulShowSourceCodeInContest(@PathVariable(value = "cid") Long
                                                           cid, @PathVariable(value = "id") Long id) {
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
    public Page<Solution> getUserSolutions(@PathVariable("cid") Long cid,
                                           @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            @NotNull User user = (User) session.getAttribute("currentUser");
//            User user = userService.getUserById(5l); // test
            @NotNull Page<Solution> solutions = solutionService.getSolutionsOfUserInContest(page, PAGE_SIZE, user, contest);
            Map<Long, ContestProblem> cpmap = new HashMap<>();
            for (ContestProblem cp : contest.getProblems()) {
                cpmap.put(cp.getProblem().getId(), cp);
            }
            for (Solution s : solutions.getContent()) {
                s.setSource(null);
                s.setIp(null);
                s.getUser().setEmail(null);
                s.getUser().setPassword(null);
                s.getUser().setIntro(null);
                Problem tp = Problem.jsonReturnProblemFactory();
                tp.setId(cpmap.get(s.getProblem().getId()).getTempId());
                s.setProblem(tp);
                s.setContest(null);
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

    @GetMapping("/ranklist/{cid}")
    public Rank getRankOfContest(@PathVariable Long cid) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            @NotNull Rank rank = new Rank(contest);
            @NotNull List<Solution> solutions = solutionService.getSolutionsInContest(contest);
            for (Solution s : solutions) {
                rank.update(s);
            }
            return rank;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new NotFoundException();
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