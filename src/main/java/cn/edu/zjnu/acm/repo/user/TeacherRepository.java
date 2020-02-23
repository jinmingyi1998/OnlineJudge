package cn.edu.zjnu.acm.repo.user;

import cn.edu.zjnu.acm.entity.Teacher;
import cn.edu.zjnu.acm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser(User user);

    boolean existsByUser(User user);
}
