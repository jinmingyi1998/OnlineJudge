package cn.edu.zjnu.learncs.entity.learn;

import com.jinmy.onlinejudge.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class UserQuestion {
    public static final String WRONG="wrong";
    public static final String RIGHT="right";
    public UserQuestion() {
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne
    private Question question;
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private Instant date;
    @Column(nullable = false)
    public String result;
}
