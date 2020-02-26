package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Article;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.article.ArticleCommentRepository;
import cn.edu.zjnu.acm.repo.article.ArticleRepository;
import cn.edu.zjnu.acm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog")
public class ArticleController {
    private final Config ojConfig;
    private final ArticleRepository articleRepository;
    @Autowired
    private UserService userService;
    private final ArticleCommentRepository articleCommentRepository;
    private static final int SIZE = 30;

    public ArticleController(ArticleRepository articleRepository, ArticleCommentRepository articleCommentRepository, Config ojConfig) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.ojConfig = ojConfig;
    }

    private Article getArticleById(Long aid){
        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            throw new NotFoundException();
        }
        return article;
    }

    @GetMapping("")
    public Page<Article> getArticleList(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "search", defaultValue = "") String search) {
        if (search.length() == 0) {
            return articleRepository.findAll(PageRequest.of(page, SIZE, Sort.by(Sort.Direction.DESC, "id")));
        }
        return articleRepository.findAllByTitleContaining(PageRequest.of(page, SIZE, Sort.by(Sort.Direction.DESC, "id")), search);
    }

    @GetMapping("/{aid:[0-9]+}")
    public Article getArticle(@PathVariable Long aid) {
        Article article = getArticleById(aid);
        article.getUser().hideInfo();
        return article;
    }

    @PostMapping("/post")
    public String postArticle(@RequestBody @Validated Article article, @SessionAttribute(required = false) User currentUser) {
        if (currentUser == null) {
            throw new NeedLoginException();
        }
        if (userService.getUserPermission(currentUser)>=0)
        if (currentUser.getUserProfile().getScore() < ojConfig.getLeastScoreToPostBlog()) {
            return "score too low, at least " + ojConfig.getLeastScoreToPostBlog();
        }
        try {
            article.setUser(currentUser);
            article.setComment(null);
            articleRepository.save(article);
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown error";
        }
        return "success";
    }

    @PostMapping("/edit/{aid:[0-9]+}")
    public String editArticle(@RequestBody @Validated  Article editArticle,
                              @PathVariable Long aid,
                              @SessionAttribute User currentUser) {
        Article article = getArticleById(aid);
        if (article.getUser().getId() != currentUser.getId()) {
            throw new ForbiddenException();
        }
        try {
            article.setTitle(editArticle.getTitle());
            article.setText(editArticle.getText());
            articleRepository.save(article);
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown error";
        }
        return "success";
    }
}

@Controller
@RequestMapping("/blog")
class ArticleViewController {
    @GetMapping("")
    public String listArticle(){
        return "blog/bloglist";
    }
    @GetMapping("/{}")
    public String showArticle(){
        return "blog/show";
    }
    @GetMapping("/edit/{}")
    public String editArticle(){
        return "blog/edit";
    }
    @GetMapping("/post")
    public String postArticle(){
        return "blog/post";
    }
}
