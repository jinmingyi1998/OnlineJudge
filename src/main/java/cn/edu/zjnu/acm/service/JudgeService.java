package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestProblem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.entity.oj.UserProblem;
import cn.edu.zjnu.acm.repo.contest.ContestProblemRepository;
import cn.edu.zjnu.acm.repo.problem.ProblemRepository;
import cn.edu.zjnu.acm.repo.user.UserProblemRepository;
import cn.edu.zjnu.acm.repo.user.UserProfileRepository;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JudgeService {
    private final Config config;
    private final RESTService restService;
    private final SolutionService solutionService;
    private final UserProfileRepository userProfileRepository;
    private final ProblemRepository problemRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestService contestService;
    private final UserProblemRepository userProblemRepository;

    public JudgeService(Config config, RESTService restService, SolutionService solutionService, UserProfileRepository userProfileRepository, ProblemRepository problemRepository, ContestProblemRepository contestProblemRepository, ContestService contestService, UserProblemRepository userProblemRepository) {
        this.config = config;
        this.restService = restService;
        this.solutionService = solutionService;
        this.userProfileRepository = userProfileRepository;
        this.problemRepository = problemRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.contestService = contestService;
        this.userProblemRepository = userProblemRepository;
    }

    public String submitCode(Solution solution) throws Exception {
        String host = config.getJudgerhost().get(solution.getId().intValue() % config.getJudgerhost().size());
        Config.LanguageConfig language;
        switch (solution.getLanguage()) {
            case "c":
                language = config.getC();
                break;
            case "cpp":
                language = config.getCpp();
                break;
            case "java":
                language = config.getJava();
                break;
            case "py2":
                language = config.getPython2();
                break;
            case "py3":
                language = config.getPython3();
                break;
            case "go":
                language = config.getGo();
                break;
            default:
                throw new Exception("no this language");
        }
        if (!(solution.getLanguage().equals("c") || solution.getLanguage().equals("cpp"))) {
            solution.getProblem().setTimeLimit(solution.getProblem().getTimeLimit() * 2);
            solution.getProblem().setMemoryLimit(solution.getProblem().getMemoryLimit() * 2);
        }
        JudgeService.SubmitCode submitCode = new JudgeService.SubmitCode(
                solution.getId().intValue(),
                solution.getProblem().getId().intValue(),
                solution.getProblem().getTimeLimit(),
                solution.getProblem().getMemoryLimit(),
                language.getMemory_limit_check_only(),
                language.getSrc(),
                language.getSeccomp_rule(),
                language.getRun_command(),
                language.getCompile_command(),
                solution.getSource()
        );
        log.info(host);
        log.info(language.toString());
        String jsonString = JSON.toJSONString(submitCode);
        return restService.postJson(jsonString, host);
    }

    @Transactional
    public void update(Solution solution) {
        submitSolutionFilter(solution);
        acceptSolutionFilter(solution);
        contestSolutionFilter(solution);
    }

    private void submitSolutionFilter(Solution solution) {
        solutionService.updateSolutionResultTimeMemoryCase(solution);
        userProfileRepository.updateUserSubmitted(solution.getUser().getUserProfile().getId(), 1);
        problemRepository.updateSubmittedNumber(solution.getProblem().getId(), 1);
    }

    private void contestSolutionFilter(Solution solution) {
        if (solution.getContest() != null) {
            Contest contest = contestService.getContestById(solution.getContest().getId(), true);
            for (ContestProblem cp : contest.getProblems()) {
                if (cp.getProblem().getId().equals(solution.getProblem().getId())) {
                    contestProblemRepository.updateSubmittedNumber(cp.getId(), 1);
                    if (solution.getResult().equals(Solution.AC)) {
                        contestProblemRepository.updateAcceptedNumber(cp.getId(), 1);
                    }
                    break;
                }
            }
        }
    }

    private void acceptSolutionFilter(Solution solution) {
        if (solution.getResult().equals(Solution.AC)) {
            if (!userProblemRepository.existsAllByUserAndProblem(solution.getUser(), solution.getProblem())) {
                userProfileRepository.updateUserScore(solution.getUser().getUserProfile().getId(), solution.getProblem().getScore());
                userProfileRepository.updateUserAccepted(solution.getUser().getUserProfile().getId(), 1);
                problemRepository.updateAcceptedNumber(solution.getProblem().getId(), 1);
                userProblemRepository.save(new UserProblem(solution.getUser(), solution.getProblem()));
            }
        }
    }

    @Data
    public static class SubmitCode {
        private int submit_id;
        private int problem_id;
        private int max_cpu_time;
        private int max_memory;
        private String memory_limit_check_only;
        private String src;
        private String seccomp_rule;
        private String run_command;
        private String compile_command;
        private String source;

        public SubmitCode(int submit_id, int problem_id, int max_cpu_time, int max_memory, String memory_limit_check_only, String src, String seccomp_rule, String run_command, String compile_command, String source) {
            this.submit_id = submit_id;
            this.problem_id = problem_id;
            this.max_cpu_time = max_cpu_time;
            this.max_memory = max_memory;
            this.memory_limit_check_only = memory_limit_check_only;
            this.src = src;
            this.seccomp_rule = seccomp_rule;
            this.run_command = run_command;
            this.compile_command = compile_command;
            this.source = source;

        }

        @Override
        public String toString() {
            return "SubmitCode{" +
                    "submit_id=" + submit_id +
                    ", problem_id=" + problem_id +
                    ", max_cpu_time=" + max_cpu_time +
                    ", max_memory=" + max_memory +
                    ", src='" + src + '\'' +
                    ", seccomp_rule='" + seccomp_rule + '\'' +
                    ", run_command='" + run_command + '\'' +
                    ", compile_command='" + compile_command + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
}
