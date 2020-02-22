package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.oj.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment save(Comment comment);

    Optional<Comment> findById(Long id);
}