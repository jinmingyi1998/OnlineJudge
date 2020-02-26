package cn.edu.zjnu.acm.repo.article;

import cn.edu.zjnu.acm.entity.oj.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article>findAll();
    Page<Article>findAll(Pageable pageable);
    Page<Article>findAllByTitleContaining(Pageable pageable,String search);
    Optional<Article> findById(Long id);
}
