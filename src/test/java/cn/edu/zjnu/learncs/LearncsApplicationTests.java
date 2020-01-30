package cn.edu.zjnu.learncs;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.UserProfile;
import cn.edu.zjnu.learncs.repo.UserProfileRepository;
import cn.edu.zjnu.learncs.repo.UserRepository;
import cn.edu.zjnu.learncs.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LearncsApplicationTests {
    @Autowired
    UserProfileRepository userProfileRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    void contextLoads() {
        List<User> userList = userRepository.findAll();
    }

}
