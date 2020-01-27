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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Comment father = null;
    @Column(nullable = false)
    private Instant postTime;
    @ManyToOne
    private User user;
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @JsonIgnore
    @ManyToOne
    private Contest contest;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", father=" + father +
                ", postTime=" + postTime +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }

    public Comment(User user, String text, Contest contest) {
        this.user = user;
        this.text = text;
        this.contest = contest;
        postTime = Instant.now();
    }

    public Comment() {
    }

    @JsonManagedReference
    public String getNormalPostTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(postTime));
    }
}



