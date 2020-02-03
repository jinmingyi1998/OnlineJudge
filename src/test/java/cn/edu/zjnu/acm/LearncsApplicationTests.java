package cn.edu.zjnu.acm;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.repo.UserRepository;
import cn.edu.zjnu.acm.service.JudgeService;
import cn.edu.zjnu.acm.service.SolutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootTest
class LearncsApplicationTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SolutionService solutionService;
    @Test
    void contextLoads() {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        List<User> userList =userRepository.findAll();
//        for (User u:userList) {
//            System.out.println(u.toString());
//            u.setPassword(encoder.encode(u.getPassword()));
//            userRepository.save(u);
//        }
    }

}
