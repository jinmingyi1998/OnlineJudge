package cn.edu.zjnu.learncs.entity.learn;

import com.jinmy.onlinejudge.entity.Teacher;
import com.jinmy.onlinejudge.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Slf4j
public class Live {
    public Live() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String name;
    @Column
    private String pictureUrl;
    @Column
    private String playUrl;
    @OneToMany
    private List<User> userList;
    @OneToOne
    private Teacher teacher;
    @Column
    private Long cost;
    @OneToMany
    private List<CourseTag> courseTags;
}
