package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class CourseData {
    public CourseData(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 255)
    private String url;
    @Column(nullable = false,length = 255)
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String introduction;
}
