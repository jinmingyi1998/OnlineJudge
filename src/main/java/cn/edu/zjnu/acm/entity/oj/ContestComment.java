package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ContestComment implements CommentAdapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Contest contest;
    @JsonIgnore
    @OneToOne(optional = false)
    private Comment comment;

    public ContestComment() {
    }

    public ContestComment(Contest contest, Comment comment) {
        this.contest = contest;
        this.comment = comment;
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
