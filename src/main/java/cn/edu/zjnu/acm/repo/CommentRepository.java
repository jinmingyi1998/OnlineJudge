package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.oj.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}