package cn.edu.zjnu.learncs;

import cn.edu.zjnu.learncs.controller.ProblemController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearncsApplicationTests {

    @Autowired
    ProblemController problemController;
    @Test
    void contextLoads() {
        System.out.println(problemController.showProblemList(0, "$$初级").getContent().toString());
    }

}
