package cn.edu.zjnu.acm.repo.article;

import cn.edu.zjnu.acm.entity.oj.Article;
import cn.edu.zjnu.acm.entity.oj.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findAllByArticle(Article article);
}
