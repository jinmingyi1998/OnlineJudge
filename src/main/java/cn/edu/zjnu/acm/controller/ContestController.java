package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.NotFoundException;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.*;
import cn.edu.zjnu.acm.repo.CommentRepository;
import cn.edu.zjnu.acm.repo.ContestProblemRepository;
import cn.edu.zjnu.acm.service.*;
import cn.edu.zjnu.acm.util.Rank;
import lombok.Data;
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

    @GetMapping("/problem/{id}")
    public String showContest(@PathVariable(value = "id") Long id) {
        return "contest/contestinfo";
    }

    @GetMapping("/status/{id}")
    public String showContestStatus(@PathVariable(value = "id") Long id) {
        return "contest/conteststatus";
    }

    @GetMapping("/ranklist/{id}")
    public String showContestRanklist(@PathVariable(value = "id") Long id) {
        return "contest/contestrank";
    }

    @GetMapping("/comment/{id}")
    public String showContestComment(@PathVariable(value = "id") Long id) {
        return "contest/contestcomment";
    }

    @GetMapping("/{id}")
    public String contestGate(@PathVariable(value = "id") Long id) {
        return "contest/contestgate";
    }
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
    JudgeService judgeService;
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
        if (scontest == null || scontest.getId() != c.getId() || !c.isStarted()) {
            if (!c.isStarted() ||
                    (c.getPassword().length() > 0 &&
                            c.getPrivilege().equals("private") &&
                            !c.getPassword().equals(password))) {
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
                p.setSubmitted(null);
                p.setAccepted(null);
                p.setTags(null);
                p.setTitle(null);
                p.setScore(null);
                p.setSource(null);
            }
        } catch (Exception e) {
            throw new NotFoundException();
        }
        return c;
    }

    @PostMapping("/submit/{pid}/{cid}")
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
            if (contest.isEnded() || !contest.isStarted()) {
                return "The contest is not!";
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
            judgeService.submitCode(solution);
            return "success";
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @Data
    static class CommentPost {
        String rtext = "";
        Long rid = 0L;

        public Long getRid() {
            return rid == null ? 0L : rid;
        }

        public CommentPost() {
        }
    }

    @Autowired
    CommentRepository commentRepository;

    @PostMapping("/comments/post/{cid}")
    public String postComments(@PathVariable(value = "cid") Long cid, @RequestBody CommentPost commentPost) {
        try {
            if (commentPost.rtext.length() < 4) return "too short";
            User user = (User) session.getAttribute("currentUser");
            if (user == null) return "need login";
            Comment father = commentRepository.findById(commentPost.getRid()).orElse(null);
            @NotNull Contest contest = contestService.getContestById(cid);
            if (!contest.isStarted() || contest.isEnded())
                throw new NotFoundException();
            Comment comment = new Comment(user, commentPost.rtext, contest, father);
            comment = contestService.postComment(comment);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private Comment commentFilter(Comment comment) {
        comment.getUser().setPassword(null);
        comment.getUser().setEmail(null);
        comment.getUser().setIntro(null);
        comment.getUser().setUserProfile(null);
        return comment;
    }

    @GetMapping("/comments/{cid}")
    public List<Comment> getCommentsOfContest(@PathVariable Long cid) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid, false);
            if (!contest.isStarted())
                throw new NotFoundException();
            List<Comment> contestComments = contestService.getCommentsOfContest(contest);
            for (Comment c : contestComments) {
                c = commentFilter(c);
            }
            return contestComments;
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @GetMapping("/status/{cid}")
    public Page<Solution> getUserSolutions(@PathVariable("cid") Long cid,
                                           @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            @NotNull Contest contest = contestService.getContestById(cid);
            if (!contest.isStarted())
                throw new NotFoundException();
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
                s.getUser().setUserProfile(null);
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