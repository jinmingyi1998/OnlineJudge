package cn.edu.zjnu.learncs;

import cn.edu.zjnu.learncs.service.RESTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearncsApplicationTests {

    @Autowired
    RESTService servie;

    @Test
    void contextLoads() {
        System.out.println(servie.submitCode());
    }

}
