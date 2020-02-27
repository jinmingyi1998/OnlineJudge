package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public interface CommentAdapter {
    public User getUser();

    public Long getId();

    public Instant getPostTime();

    public String getText();

    public Comment getFather();

    public default String getNormalPostTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(getPostTime()));
    }
}
