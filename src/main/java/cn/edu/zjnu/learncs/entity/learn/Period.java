package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Slf4j
public class Period {
    public Period() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255, nullable = false)
    private String name;
    @OneToMany(fetch = FetchType.EAGER)
    private List<CourseData> courseData;
}
