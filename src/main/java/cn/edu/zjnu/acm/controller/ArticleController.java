package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.config.Config;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Article;
import cn.edu.zjnu.acm.entity.oj.ArticleComment;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.article.ArticleCommentRepository;
import cn.edu.zjnu.acm.repo.article.ArticleRepository;
import cn.edu.zjnu.acm.service.UserService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class ArticleController {
    private static final int SIZE = 30;

    private final Config ojConfig;
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final ArticleCommentRepository articleCommentRepository;

    public ArticleController(ArticleRepository articleRepository, ArticleCommentRepository articleCommentRepository, Config ojConfig, UserService userService) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.ojConfig = ojConfig;
        this.userService = userService;
    }

    private Article getArticleById(Long aid) {
        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            throw new NotFoundException();
        }
        return article;
    }

    @GetMapping("")
    public Page<Article> getArticleList(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "search", defaultValue = "") String search) {
        Page<Article> articles;
        if (search.length() == 0) {
            articles = articleRepository.findAll(PageRequest.of(page, SIZE, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            articles = articleRepository.findAllByTitleContaining(PageRequest.of(page, SIZE, Sort.by(Sort.Direction.DESC, "id")), search);
        }
        articles.forEach((a) -> {
            a.getUser().hideInfo();
            a.setComment(null);
        });
        return articles;
    }

    @GetMapping("/{aid:[0-9]+}")
    public Article getArticle(@PathVariable Long aid) {
        Article article = getArticleById(aid);
        article.getUser().hideInfo();
        article.setComment(articleCommentRepository.findAllByArticle(article));
        article.getComment().forEach((c) -> {
            c.getUser().hideInfo();
        });
        return article;
    }

    @PostMapping("/post")
    public ArticleCallback postArticle(@RequestBody @Validated Article article,
                                       @SessionAttribute(required = false) User currentUser) {
        if (currentUser == null) {
            throw new NeedLoginException();
        }
        if (userService.getUserPermission(currentUser) < 0)
            if (currentUser.getUserProfile().getScore() < ojConfig.getLeastScoreToPostBlog()) {
                return new ArticleCallback("score too low, at least " + ojConfig.getLeastScoreToPostBlog(), "");
            }
        try {
            article.setUser(currentUser);
            article.setComment(null);
            article = articleRepository.save(article);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArticleCallback("unknown error", "");
        }
        return new ArticleCallback("success", article.getId().toString());
    }

    @PostMapping("/edit/{aid:[0-9]+}")
    public ArticleCallback editArticle(@RequestBody @Validated Article editArticle,
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
            return new ArticleCallback("unknown error", "");
        }
        return new ArticleCallback("success", article.getId().toString());
    }

    @PostMapping("/post/comment/{aid:[0-9]+}")
    @Transactional
    public String postComment(@PathVariable Long aid,
                              @SessionAttribute User currentUser,
                              @RequestBody ContestController.CommentPost commentPost) {
        Article article = articleRepository.findById(aid).orElse(null);
        if (article == null) {
            throw new NotFoundException();
        }
        if (commentPost.replyText.length() < 4) return "too short";
        try {
            ArticleComment father = articleCommentRepository.findById(commentPost.getReplyId()).orElse(null);
            ArticleComment articleComment = new ArticleComment(currentUser, commentPost.getReplyText(), father, article);
            articleCommentRepository.save(articleComment);
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown error";
        }
        return "success";
    }


    @GetMapping("/post/score/limit")
    public String getPostScoreLimit() {
        return ojConfig.getLeastScoreToPostBlog().toString();
    }

    @Data
    class ArticleCallback {
        String message;
        String extra;

        public ArticleCallback(String message, String extra) {
            this.message = message;
            this.extra = extra;
        }

        public ArticleCallback() {
        }
    }
}

@Controller
@RequestMapping("/forum")
class ArticleViewController {
    @GetMapping("")
    public String listArticle() {
        return "forum/forumlist";
    }

    @GetMapping("/{id:[0-9]+}")
    public String showArticle() {
        return "forum/show";
    }

    @GetMapping("/edit/{id:[0-9]+}")
    public String editArticle() {
        return "forum/edit";
    }

    @GetMapping("/post")
    public String postArticle() {
        return "forum/post";
    }
}
