package cn.edu.zjnu.learncs;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.UserProfile;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import cn.edu.zjnu.learncs.repo.UserProfileRepository;
import cn.edu.zjnu.learncs.repo.UserRepository;
import cn.edu.zjnu.learncs.service.JudgeService;
import cn.edu.zjnu.learncs.service.SolutionService;
import cn.edu.zjnu.learncs.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
