package cn.edu.zjnu.acm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Data
@Configuration
@ConfigurationProperties(prefix = "onlinejudge")
public class Config {
    //TODO score should be a threshold which can be set by admin
    private Integer leastScoreToSeeOthersCode = 1000;
    private ArrayList<String> judgerhost;
    private LanguageConfig c;
    private LanguageConfig cpp;
    private LanguageConfig java;
    private LanguageConfig python2;
    private LanguageConfig python3;
    private LanguageConfig go;

    @Data
    public static class LanguageConfig {
        private String src;
        private String seccomp_rule = "";
        private String run_command;
        private String compile_command;
        private String memory_limit_check_only = "0";

        @Override
        public String toString() {
            return "LanguageConfig{" +
                    "src='" + src + '\'' +
                    ", seccomp_rule='" + seccomp_rule + '\'' +
                    ", run_command='" + run_command + '\'' +
                    ", compile_command='" + compile_command + '\'' +
                    ", memory_limit_check_only='" + memory_limit_check_only + '\'' +
                    '}';
        }
    }
}

