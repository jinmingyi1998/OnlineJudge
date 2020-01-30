package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.config.Config;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class JudgeService {
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

    @Autowired
    Config config;
    @Autowired
    RESTService restService;

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
}
