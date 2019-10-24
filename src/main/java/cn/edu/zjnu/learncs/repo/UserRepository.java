package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>  {
    User getUserById(Long id);
    User getUserByUsername(String username);
}
