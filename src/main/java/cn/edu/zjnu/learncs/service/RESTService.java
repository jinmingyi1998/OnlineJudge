package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.config.Config;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class RESTService {
    @Autowired
    Config config;

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
        SubmitCode submitCode = new SubmitCode(
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
        return postJson(jsonString, host);
    }

    public String postJson(String json, String path) {
        String result = null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(60000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            byte[] writebytes = json.getBytes();
            connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.append(json);
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

}
