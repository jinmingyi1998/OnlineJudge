package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.oj.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}
