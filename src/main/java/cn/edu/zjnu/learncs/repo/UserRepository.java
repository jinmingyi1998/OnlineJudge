package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>  {
    @Override
    Optional<User> findById(Long aLong);

    @Override
    List<User> findAll();

    @Override
    User save(User entity);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);
}
