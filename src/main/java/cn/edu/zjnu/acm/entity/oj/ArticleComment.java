package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class ArticleComment extends Comment {
    @ManyToOne(optional = false)
    private Article article;

    public ArticleComment() {
    }

    public ArticleComment(User user, String text, Comment father, Article article) {
        super(user, text, father);
        this.article = article;
    }
}
