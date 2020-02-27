package cn.edu.zjnu.acm;

import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.entity.oj.UserProblem;
import cn.edu.zjnu.acm.repo.problem.SolutionRepository;
import cn.edu.zjnu.acm.repo.user.UserProblemRepository;
import cn.edu.zjnu.acm.repo.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LearncsApplicationTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    UserProblemRepository userProblemRepository;

    @Test
    void contextLoads() {
        List<Solution> solutions = solutionRepository.findAll();
        for (Solution s : solutions) {
            if (s.getResult().equals(Solution.AC)) {
                if (!userProblemRepository.existsAllByUserAndProblem(s.getUser(), s.getProblem())) {
                    userProblemRepository.save(new UserProblem(s.getUser(), s.getProblem()));
                }
            }
        }
    }

}
