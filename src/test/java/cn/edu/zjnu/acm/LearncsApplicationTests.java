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
        String code = generateCode(123);
        System.out.println(code);
        System.out.println(decode(code));
    }
    private String generateCode(int number){
        int offset = (int) ((Math.random()*10)%10);
        StringBuffer sub = new StringBuffer();
        sub.append((char)(offset+65));
        for(int i = 1;i<18;i++){
            char c = (char) (Math.random()*26+65);
            sub.append(c);
        }
        String str = String.format("%06d",number);
        for(int i=1;i<=6;i++)
            sub.setCharAt(offset+i, (char) (str.charAt(i-1)+17+offset));
        return String.valueOf(sub);
    }
    private int decode(String s){
        int offset = s.charAt(0)-65;
        int result=0;
        for(int i=1;i<=6;i++)
        {
            result*=10;
            int n = s.charAt(offset+i)-65-offset;
            result+=n;
        }
        return result;
    }
}
