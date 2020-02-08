package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long aLong);

    @Override
    List<User> findAll();

    @Override
    User save(User entity);

    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET email=:email ,intro=:intro ,name=:name , password=:password WHERE id=:id"
            , nativeQuery = true)
    void updateUser(@Param("id") Long id, @Param(value = "name") String name,
                    @Param("password") String password, @Param("email") String email,
                    @Param("intro") String intro);
}
