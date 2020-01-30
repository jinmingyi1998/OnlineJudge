package cn.edu.zjnu.learncs.entity.oj;

import cn.edu.zjnu.learncs.entity.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comment implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment father = null;
    @Column(nullable = false)
    private Instant postTime;
    @ManyToOne(optional = false)
    private User user;
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @JsonIgnore
    @ManyToOne
    private Contest contest;

    public Long getFatherId() {
        return father == null ? null : father.getId();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", father=" + getFatherId() +
                ", postTime=" + postTime +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }

    public Comment(User user, String text, Contest contest, Comment father) {
        this.user = user;
        this.text = text;
        this.contest = contest;
        this.father = father;
        postTime = Instant.now();
    }

    public Comment() {
    }

    @JsonManagedReference
    public String getNormalPostTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(postTime));
    }

    @Override
    public int compareTo(Object o) {
        Comment c = (Comment) o;
        return getPostTime().compareTo(c.getPostTime()) * -1;
    }
}



