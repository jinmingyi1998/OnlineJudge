package cn.edu.zjnu.acm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LearncsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearncsApplication.class, args);
    }
    /*
    TODO:
    名字颜色可变色
     */

}
