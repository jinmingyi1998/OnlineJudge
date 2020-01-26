package cn.edu.zjnu.learncs;

import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.repo.ContestRepository;
import cn.edu.zjnu.learncs.repo.ProblemRepository;
import cn.edu.zjnu.learncs.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class LearncsApplicationTests {

    @Autowired
    ContestRepository contestRepository;
    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    @Transactional
    void contextLoads() {
        Contest ac = contestRepository.findById(1l).get();
        System.out.println(ac.getProblems().toString());
    }

}
