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
public class ArticleComment implements CommentAdapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Article article;
    @JsonIgnore
    @OneToOne(optional = false)
    private Comment comment;

    public ArticleComment() {
    }

    @Override
    public User getUser() {
        return comment.getUser();
    }

    @Override
    public Long getId() {
        return comment.getId();
    }

    @Override
    public Instant getPostTime() {
        return comment.getPostTime();
    }

    @Override
    public String getText() {
        return comment.getText();
    }

    @Override
    public Comment getFather() {
        return comment.getFather();
    }
}
