package cn.edu.zjnu.acm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "onlinejudge.switches")
public class JudgeSwitchConfig {
    private Boolean executing;
    private Boolean submit;
    private Boolean enterSystem;
    private Boolean sslEnabled;
    private Boolean autoCreateDir;
}
