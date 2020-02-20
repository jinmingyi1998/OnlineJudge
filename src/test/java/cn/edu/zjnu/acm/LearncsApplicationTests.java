package cn.edu.zjnu.acm;

import cn.edu.zjnu.acm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class LearncsApplicationTests {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        User user = getUser();
        ValueOperations<String, User> operations=redisTemplate.opsForValue();
        operations.set("user", user);
        User t = operations.get("user");
        System.out.println(t.toString());
    }

    public User getUser() {
        User user = new User();
        user.setId(123L);
        user.setName("2213");
        user.setPassword("awef");
        user.setUsername("2rhu");
        user.setEmail("dsf@s.c");
        user.setIntro("dasfjkhsd");
        return user;
    }

}
