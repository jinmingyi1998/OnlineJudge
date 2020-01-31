package cn.edu.zjnu.acm;

import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.service.JudgeService;
import cn.edu.zjnu.acm.service.SolutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearncsApplicationTests {
    @Autowired
    JudgeService judgeService;
    @Autowired
    SolutionService solutionService;
    @Test
    void contextLoads() {
        Solution s = solutionService.getSolutionById(1l);
        judgeService.update(s);
    }

}
