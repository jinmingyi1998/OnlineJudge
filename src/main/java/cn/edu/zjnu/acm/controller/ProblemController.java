package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Analysis;
import cn.edu.zjnu.acm.entity.oj.AnalysisComment;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.user.UserProblemRepository;
import cn.edu.zjnu.acm.service.JudgeService;
import cn.edu.zjnu.acm.service.ProblemService;
import cn.edu.zjnu.acm.service.SolutionService;
import cn.edu.zjnu.acm.service.UserService;
import cn.edu.zjnu.acm.util.RestfulResult;
import cn.edu.zjnu.acm.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequestMapping("/problems")
@Controller
class ProblemViewController {
    @GetMapping
    public String problemsList() {
        return "problem/problemlist";
    }

    @GetMapping("/{id:[0-9]+}")
    public String showProblem(@PathVariable Long id) {
        return "problem/showproblem";
    }

    @GetMapping("/article/{id:[0-9]+}")
    public String problemArticle() {
        return "problem/article";
    }

    @GetMapping("/article/edit/{id:[0-9]+}")
    public String editArticle() {
        return "problem/edit_analysis";
    }
}

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/problems")
public class ProblemController {
    private static final int PAGE_SIZE = 30;
    private final ProblemService problemService;
    private final UserService userService;
    private final JudgeService judgeService;
    private final SolutionService solutionService;
    private final HttpSession session;

    public ProblemController(ProblemService problemService, UserProblemRepository userProblemRepository, UserService userService, JudgeService judgeService, SolutionService solutionService, HttpSession session) {
        this.problemService = problemService;
        this.userService = userService;
        this.judgeService = judgeService;
        this.solutionService = solutionService;
        this.session = session;
    }

    static String checkSubmitFrequncy(HttpSession session, String source) {
        if (session.getAttribute("last_submit") != null) {
            Instant instant = (Instant) session.getAttribute("last_submit");
            if (Instant.now().minusSeconds(10).compareTo(instant) < 0) {
                return "Don't submitted within 10 seconds";
            } else if (source.length() > 20000) {
                return "Source code too long";
            } else if (source.length() < 2) {
                return "Source code too short";
            }
        }
        session.setAttribute("last_submit", Instant.now());
        return null;
    }

    Problem checkProblemExist(Long pid) {
        Problem problem = problemService.getProblemById(pid);
        if (problem == null) {
            throw new NotFoundException("No Problem Found");
        }
        return problem;
    }

    @GetMapping("")
    public RestfulResult showProblemList(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "search", defaultValue = "") String search) {
        page = Math.max(page, 0);
        Page<Problem> problemPage;
        if (search != null && search.length() > 0) {
            int spl = search.lastIndexOf("$$");
            if (spl >= 0) {
                String tags = search.substring(spl + 2);
                search = search.substring(0, spl);
                String[] tagNames = tags.split("\\,");
                List<Problem> _problems = problemService.searchActiveProblem(0, 1, search, true).getContent();
                problemPage = problemService.getByTagName(page, PAGE_SIZE, Arrays.asList(tagNames), _problems);
            } else {
                problemPage = problemService.searchActiveProblem(page, PAGE_SIZE, search, false);
            }
        } else {
            problemPage = problemService.getAllActiveProblems(page, PAGE_SIZE);
        }
        for (Problem p : problemPage.getContent()) {
            p.setInput(null);
            p.setOutput(null);
            p.setHint(null);
            p.setSource(null);
            p.setSampleInput(null);
            p.setSampleOutput(null);
        }
        return new RestfulResult(200, "success", problemPage);
    }

    @GetMapping("/{id:[0-9]+}")
    public RestfulResult showProblem(@PathVariable Long id) {
        Problem problem = problemService.getActiveProblemById(id);
        if (problem == null)
            throw new NotFoundException();
        return new RestfulResult(200, "success", problem);
    }

    @GetMapping("/name/{id:[0-9]+}")
    public String getProblemName(@PathVariable(value = "id") Long id) {
        try {
            return ((Problem) showProblem(id).getData()).getTitle();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/submit/{id:[0-9]+}")
    public Result submitProblem(@PathVariable("id") Long id,
                                @RequestBody SubmitCodeObject submitCodeObject,
                                HttpServletRequest request) {
        String source = submitCodeObject.getSource();
        boolean share = submitCodeObject.isShare();
        String language = submitCodeObject.getLanguage();
        String _temp = checkSubmitFrequncy(session, source);
        if (_temp != null)
            return new Result(403, _temp);
        User user = (User) session.getAttribute("currentUser");
        if (user == null || userService.getUserById(user.getId()) == null) {
            throw new NeedLoginException();
        }
        Problem problem = problemService.getActiveProblemById(id);
        if (problem == null) {
            throw new NotFoundException("Problem Not Exist");
        }
        //null检验完成
        Solution solution = solutionService.insertSolution(new Solution(user, problem, language, source, request.getRemoteAddr(), share));
        if (solution == null)
            return new Result(400, "submitted failed");
        try {
//            return restService.submitCode(solution) == null ? "judge failed" : "success";
            judgeService.submitCode(solution);
            return new Result(200, "success");
        } catch (Exception e) {
            return new Result(500, "Internal error");
        }
    }

    @GetMapping("/tags")
    public RestfulResult showTags() {
        return new RestfulResult(200, "success", problemService.getAllTags());
    }

    @GetMapping("/is/accepted/{pid:[0-9]+}")
    public RestfulResult checkUserHasAc(@SessionAttribute User currentUser, @PathVariable Long pid) {
        Problem problem = checkProblemExist(pid);
        return new RestfulResult(200,
                "success",
                problemService.isUserAcProblem(currentUser, problem));
    }

    @GetMapping("/analysis/{pid:[0-9]+}")
    public RestfulResult getProblemArticle(@PathVariable Long pid,
                                           @SessionAttribute User currentUser) {
        Problem problem = checkProblemExist(pid);
        if (userService.getUserPermission(currentUser) == -1) {
            if (!problemService.isUserAcProblem(currentUser, problem)) {
                throw new ForbiddenException("Access after passing the question");
            }
        }
        List<Analysis> analyses = problemService.getAnalysisByProblem(problem);
        analyses.forEach(a -> a.getUser().hideInfo());
        return new RestfulResult(200, "success", analyses);
    }

    @PostMapping("/analysis/post/{pid:[0-9]+}")
    public RestfulResult postAnalysis(@PathVariable Long pid,
                                      @SessionAttribute User currentUser,
                                      @RequestBody @Validated Analysis analysis) {
        Problem problem = checkProblemExist(pid);
        if (userService.getUserPermission(currentUser) == -1) {
            if (!problemService.isUserAcProblem(currentUser, problem)) {
                throw new ForbiddenException("Access after passing the question");
            }
        }
        analysis.setUser(currentUser);
        analysis.setComment(null);
        analysis.setPostTime(Instant.now());
        analysis.setProblem(problem);
        problemService.postAnalysis(analysis);
        return new RestfulResult(200, "success", null);
    }

    @GetMapping("/analysis/edit/{aid:[0-9]+}")
    public RestfulResult getOneAnalysis(@PathVariable Long aid, @SessionAttribute User currentUser) {
        Analysis analysis = problemService.getAnalysisById(aid);
        if (analysis == null) {
            throw new NotFoundException("Analysis not found");
        }
        if (userService.getUserPermission(currentUser) == -1) {
            if (analysis.getUser().getId() != currentUser.getId()) {
                throw new ForbiddenException("Permission denied");
            }
        }
        analysis.getUser().hideInfo();
        analysis.setProblem(null);
        analysis.setComment(null);
        return new RestfulResult(200, "success", analysis);
    }

    @PostMapping("/analysis/edit/{aid:[0-9]+}")
    public RestfulResult editAnalysis(@PathVariable Long aid,
                                      @SessionAttribute User currentUser,
                                      @RequestBody @Validated Analysis analysis) {
        Analysis ana = problemService.getAnalysisById(aid);
        if (analysis == null) {
            throw new NotFoundException("Analysis not found");
        }
        if (userService.getUserPermission(currentUser) == -1) {
            if (ana.getUser().getId() != currentUser.getId()) {
                throw new ForbiddenException("Permission denied");
            }
        }
        ana.setText(analysis.getText());
        problemService.postAnalysis(ana);
        return new RestfulResult(200, "success", null);
    }

    @PostMapping("/analysis/post/comment/{aid:[0-9]+}")
    public RestfulResult postAnalysisComment(@PathVariable Long aid,
                                             @SessionAttribute User currentUser,
                                             @RequestBody ContestController.CommentPost commentPost) {
        if (commentPost.replyText.length() < 4) {
            return new RestfulResult(400, "bad request", "too short!");
        }
        Analysis analysis = problemService.getAnalysisById(aid);
        if (analysis == null) {
            throw new NotFoundException("Analysis not found");
        }
        if (userService.getUserPermission(currentUser) == -1) {
            if (!problemService.isUserAcProblem(currentUser,
                    checkProblemExist(analysis.getProblem().getId()))) {
                throw new ForbiddenException("Access after passing the question");
            }
        }
        AnalysisComment father = problemService.getFatherComment(commentPost.getReplyId());
        problemService.postAnalysisComment(new AnalysisComment(currentUser, commentPost.replyText, father, analysis));
        return new RestfulResult(200, "success", null);
    }

    public static class SubmitCodeObject {
        private String source;
        private String language;
        private String share;

        public SubmitCodeObject() {
        }

        public Boolean isShare() {
            return share.equals("true");
        }

        public void setShare(String share) {
            this.share = share;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
