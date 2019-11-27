package cn.edu.zjnu.learncs.entity.learn;

import com.jinmy.onlinejudge.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;

@Data
@Slf4j
@Entity
public class LiveComment {
    public LiveComment() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private User user;
    @Column
    private Instant time;
    @Column(length = 250)
    private String content;
    @ManyToOne
    private Live live;
}