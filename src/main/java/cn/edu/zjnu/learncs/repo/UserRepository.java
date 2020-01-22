package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>  {
    @Override
    Optional<User> findById(Long aLong);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
}
