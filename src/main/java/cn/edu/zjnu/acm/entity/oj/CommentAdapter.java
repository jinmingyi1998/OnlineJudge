package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;

import java.time.Instant;

public interface CommentAdapter {
    public User getUser();

    public Long getId();

    public Instant getPostTime();

    public String getText();

    public Comment getFather();
}
