package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.entity.oj.Solution;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
@Service
public class RESTService {

    @Data
    public static class SubmitCode {
        private int submit_id;
        private int problem_id;
        private int max_cpu_time;
        private int max_memory;
        private String src;
        private String seccomp_rule;
        private String run_command;
        private String compile_command;
        private String source;

        public SubmitCode(int submit_id, int problem_id, int max_cpu_time, int max_memory, String src, String seccomp_rule, String run_command, String compile_command, String source) {
            this.submit_id = submit_id;
            this.problem_id = problem_id;
            this.max_cpu_time = max_cpu_time;
            this.max_memory = max_memory;
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

    public String submitCode(Solution solution) {
        SubmitCode submitCode = new SubmitCode(
                solution.getId().intValue(),
                solution.getProblem().getId().intValue(),
                solution.getProblem().getTimeLimit(),
                solution.getProblem().getMemoryLimit(),
                "main.cpp",
                "c_cpp",
                "./main",
                "/usr/bin/g++ main.cpp -lm -o main",
                "\"#include<stdio.h>\\nint main()\\n{\\n\\tint a,b;\\n\\tscanf(\\\"%d %d\\\",&a,&b);\\n\\tprintf(\\\"%d\\\\n\\\",a+b);\\n}\\n\"\n}"
        );
        String jsonString = JSON.toJSONString(submitCode);
        return postJson(jsonString, "http://localhost:12345/judge");
    }

    public String postJson(String json, String path) {
        String result = "";
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
            e.printStackTrace();
            return null;
        }
        return result;
    }

}
