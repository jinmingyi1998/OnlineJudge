package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.config.GlobalStatus;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestProblem;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.contest.ContestProblemRepository;
import cn.edu.zjnu.acm.repo.problem.ProblemRepository;
import cn.edu.zjnu.acm.repo.problem.SolutionRepository;
import cn.edu.zjnu.acm.repo.user.UserProfileRepository;
import cn.edu.zjnu.acm.service.ContestService;
import cn.edu.zjnu.acm.service.ProblemService;
import cn.edu.zjnu.acm.service.SolutionService;
import cn.edu.zjnu.acm.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {
    public static final int PAGE_SIZE = 50;
    private final ProblemService problemService;
    private final ContestService contestService;
    private final UserService userService;
    private final HttpSession session;
    private final ProblemRepository problemRepository;
    private final Config config;
    private final SolutionService solutionService;
    private final ContestProblemRepository contestProblemRepository;
    private final SolutionRepository solutionRepository;
    private final UserProfileRepository userProfileRepository;

    public AdminController(ProblemService problemService, ContestService contestService, UserService userService, HttpSession session, Config config, SolutionService solutionService, ProblemRepository problemRepository, ContestProblemRepository contestProblemRepository, SolutionRepository solutionRepository, UserProfileRepository userProfileRepository) {
        this.problemService = problemService;
        this.contestService = contestService;
        this.userService = userService;
        this.session = session;
        this.config = config;
        this.solutionService = solutionService;
        this.problemRepository = problemRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.solutionRepository = solutionRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/config")
    public UpdateConfig getConfig() {
        return new UpdateConfig(config);
    }

    @PostMapping("/config")
    public String updateConfig(@RequestBody UpdateConfig updateConfig) {
        log.info(updateConfig.toString());
        config.setLeastScoreToSeeOthersCode(updateConfig.getLeastScoreToSeeOthersCode());
        config.setJudgerhost(updateConfig.getJudgerhost());
        config.setC(updateConfig.getC());
        config.setCpp(updateConfig.getCpp());
        config.setJava(updateConfig.getJava());
        config.setPython2(updateConfig.getPython2());
        config.setPython3(updateConfig.getPython3());
        config.setGo(updateConfig.getGo());
        config.setNotice(updateConfig.getNotice());
        return "success";
    }

    @GetMapping("/problem")
    public Page<Problem> getAllProblems(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "search", defaultValue = "") String search) {
        page = Math.max(page, 0);
        Page<Problem> problemPage;
        problemPage = problemService.getAllProblems(page, PAGE_SIZE, search);
        return problemPage;
    }

    @PostMapping("/problem/insert")
    public String addProblem(@RequestBody JsonProblem problem) {
        if (problemService.isProblemRepeated(problem.getTitle())) {
            return "Problem name already existed!";
        }
        Problem p = new Problem(problem.getTitle(), problem.getDescription(),
                problem.getInput(), problem.getOutput(), problem.getSampleInput(),
                problem.getSampleOutput(), problem.getHint(), problem.getSource(),
                problem.getTime(), problem.getMemory(), problem.getScore());
        p.setActive(problem.getActive());
        p.setTags(problemService.convertString2Tag(problem.getTags()));
        problemService.insertNewProblem(p);
        return "success";
    }

    @PostMapping("/problem/edit/{pid:[0-9]+}")
    public String updateProblem(@RequestBody JsonProblem problem, @PathVariable("pid") Long pid) {
        Problem p = problemService.getProblemById(pid);
        if (null == p) {
            return "Problem not existed!";
        }
        p.setTitle(problem.getTitle());
        p.setDescription(problem.getDescription());
        p.setInput(problem.getInput());
        p.setOutput(problem.getOutput());
        p.setSampleInput(problem.getSampleInput());
        p.setSampleOutput(problem.getSampleOutput());
        p.setHint(problem.getHint());
        p.setSource(problem.getSource());
        p.setTimeLimit(problem.getTime());
        p.setMemoryLimit(problem.getMemory());
        p.setScore(problem.getScore());
        p.setActive(problem.getActive());
        p.setTags(problemService.convertString2Tag(problem.getTags()));
        problemService.insertNewProblem(p);
        return "success";
    }

    @GetMapping("/problem/{id:[0-9]+}")
    public Problem showProblem(@PathVariable Long id) {
        Problem problem = problemService.getProblemById(id);
        if (problem == null)
            throw new NotFoundException();
        return problem;
    }

    @GetMapping("/correctData")
    public String calculateData() {
        try {
            User user = (User) session.getAttribute("currentUser");
            log.info("calculating data by user:" + user.getUsername());
            Thread main = new Thread(() -> {
                Thread threadProblem = new Thread(this::calcProblem);
                Thread threadContest = new Thread(this::calcContest);
                Thread threadUser = new Thread(this::calcUser);
                threadContest.start();
                threadProblem.start();
                threadUser.start();
                try {
                    threadContest.join();
                    threadProblem.join();
                    threadUser.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    log.info("calculating finished");
                    GlobalStatus.maintaining = false;
                }
            });
            GlobalStatus.maintaining = true;
            main.start();
            return "将持续一段时间, it will cost a long long time...";
        } catch (Exception e) {
            log.info("exception catched");
        }
        return "failed";
    }

    @Transactional
    void calcUser() {
        log.info("calculating on user");
        List<User> userList = userService.userList();
        int cnt = 0;
        for (User u : userList) {
            if (++cnt % 100 == 0)
                log.info("update user " + u.getId());
            userProfileRepository.setUserSubmitted(u.getUserProfile().getId(), solutionRepository.countAllByUser(u).intValue());
            userProfileRepository.setUserAccepted(u.getUserProfile().getId(), solutionRepository.countAllByUserAndResult(u, Solution.AC).intValue());
            userProfileRepository.setUserScore(u.getUserProfile().getId(), solutionRepository.calculateScoreOfUser(u.getId()).intValue());
        }
        log.info("calculating on user finished");
    }

    @Transactional
    void calcProblem() {
        log.info("calculating on problem");
        List<Problem> problemList = problemService.getProblemList();
        int cnt = 0;
        for (Problem p : problemList) {
            if (++cnt % 100 == 0) {
                log.info("update problem " + p.getId());
            }
            problemRepository.setAcceptedNumber(p.getId(), solutionService.countAcOfProblem(p).intValue());
            problemRepository.setSubmittedNumber(p.getId(), solutionService.countOfProblem(p).intValue());
        }
        log.info("calculating on problem finished");
    }

    @Transactional
    void calcContest() {
        log.info("calculating on contest");
        int cnt = 0;
        List<Contest> contestList = contestService.getContestList();
        for (Contest c : contestList) {
            if (++cnt % 100 == 0)
                log.info("update contest " + c.getId());
            List<ContestProblem> contestProblemList = contestProblemRepository.findAllByContest(c);
            for (ContestProblem cp : contestProblemList) {
                cp.setSubmitted(solutionService.countOfProblemContest(cp.getProblem(), c).intValue());
                cp.setAccepted(solutionService.countAcOfProblemContest(cp.getProblem(), c).intValue());
                contestProblemRepository.save(cp);
            }
        }
        log.info("calculating on contest finished");
    }

    @GetMapping("/maintain")
    public String maintainSwich() {
        GlobalStatus.teacherOnly ^= true;
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new ForbiddenException();
        }
        log.info(user.getId() + "set status: teacherOnly to " + GlobalStatus.teacherOnly);
        return GlobalStatus.teacherOnly ? "maintaining now" : "not maintaining now";
    }


    @Data
    static class UpdateConfig {
        private Integer leastScoreToSeeOthersCode = 1000;
        private ArrayList<String> judgerhost;
        private Config.LanguageConfig c;
        private Config.LanguageConfig cpp;
        private Config.LanguageConfig java;
        private Config.LanguageConfig python2;
        private Config.LanguageConfig python3;
        private Config.LanguageConfig go;
        private String notice;

        public UpdateConfig() {
        }

        public UpdateConfig(Integer leastScoreToSeeOthersCode, ArrayList<String> judgerhost, Config.LanguageConfig c, Config.LanguageConfig cpp, Config.LanguageConfig java, Config.LanguageConfig python2, Config.LanguageConfig python3, Config.LanguageConfig go) {
            this.leastScoreToSeeOthersCode = leastScoreToSeeOthersCode;
            this.judgerhost = judgerhost;
            this.c = c;
            this.cpp = cpp;
            this.java = java;
            this.python2 = python2;
            this.python3 = python3;
            this.go = go;
        }

        public UpdateConfig(Config config) {
            setLeastScoreToSeeOthersCode(config.getLeastScoreToSeeOthersCode());
            setJudgerhost(config.getJudgerhost());
            setC(config.getC());
            setCpp(config.getCpp());
            setJava(config.getJava());
            setPython2(config.getPython2());
            setPython3(config.getPython3());
            setGo(config.getGo());
            setNotice(config.getNotice());
        }
    }

    @Data
    private static class JsonProblem {
        private String title;
        private String description;
        private String input;
        private String output;
        private String sampleInput;
        private String sampleOutput;
        private String hint;
        private String source;
        private Integer time;
        private Integer memory;
        private Boolean active;
        private Integer score;
        private String tags;

        public JsonProblem() {
        }
    }

}


@Controller
@RequestMapping("/admin")
class AdminViewController {
    @GetMapping
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/problem")
    public String getAllProblem() {
        return "admin/admin";
    }

    @GetMapping("/problem/edit/{pid:[0-9]+}")
    public String editProblem() {
        return "admin/edit";
    }

    @GetMapping("/problem/add")
    public String addProblem() {
        return "admin/insert";
    }

    @GetMapping("/settings")
    public String setting() {
        return "admin/setting";
    }
}
