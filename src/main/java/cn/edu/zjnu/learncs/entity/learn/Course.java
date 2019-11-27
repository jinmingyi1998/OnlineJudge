package cn.edu.zjnu.learncs.entity.learn;

import com.jinmy.onlinejudge.entity.Teacher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Slf4j
@Entity
@Data
public class Course {
    public Course(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 255)
    private String pictureUrl;
    @Column(nullable = false,length = 255)
    private String name;
    @Column
    private Instant startTime;
    @Column
    private Instant endTime;
    @Column(nullable = false,columnDefinition = "BIGINT default 0")
    private Long score;
    @Column(columnDefinition = "LONGTEXT")
    private String introduction;
    @Column(columnDefinition = "BIT default 0")
    private Boolean active;
    @ManyToOne
    private Teacher owner;
    @ManyToMany
    private List<Teacher>teachers;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Chapter> chapters;
}
