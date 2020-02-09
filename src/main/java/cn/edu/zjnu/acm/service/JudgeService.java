package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestProblem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.repo.ContestProblemRepository;
import cn.edu.zjnu.acm.repo.ProblemRepository;
import cn.edu.zjnu.acm.repo.UserProfileRepository;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class JudgeService {
    @Autowired
    private Config config;
    @Autowired
    private RESTService restService;
    @Autowired
    private SolutionService solutionService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ContestProblemRepository contestProblemRepository;
    @Autowired
    private ContestService contestService;

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
        solutionService.updateSolutionResultTimeMemoryCase(solution);
        userProfileRepository.updateUserSubmitted(solution.getUser().getUserProfile().getId(), 1);
        problemRepository.updateSubmittedNumber(solution.getProblem().getId(), 1);
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
        if (solution.getResult().equals(Solution.AC)) {
            List<Solution> solutions = solutionService.getProblemSubmitOfUser(solution.getUser(), solution.getProblem());
            boolean hasAc = false;
            for (Solution s : solutions) {
                if (s.getId().equals(solution.getId())) continue;
                if (s.getResult().equals(Solution.AC)) {
                    hasAc = true;
                }
            }
            if (!hasAc) {
                userProfileRepository.updateUserScore(solution.getUser().getUserProfile().getId(), solution.getProblem().getScore());
                userProfileRepository.updateUserAccepted(solution.getUser().getUserProfile().getId(), 1);
                problemRepository.updateAcceptedNumber(solution.getProblem().getId(), 1);
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
