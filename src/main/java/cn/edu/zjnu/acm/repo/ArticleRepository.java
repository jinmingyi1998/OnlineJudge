package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.oj.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
