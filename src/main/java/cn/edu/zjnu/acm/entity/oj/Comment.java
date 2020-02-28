package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Comment implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne
    protected Comment father = null;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    protected Instant postTime;
    @ManyToOne(optional = false)
    protected User user;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    protected String text;

    public Comment(User user, String text, Comment father) {
        this.user = user;
        this.text = text;
        this.father = father;
        postTime = Instant.now();
    }

    public Comment() {
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postTime=" + postTime +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Comment c = (Comment) o;
        return getPostTime().compareTo(c.getPostTime()) * -1;
    }
    public String getNormalPostTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(getPostTime()));
    }
}



