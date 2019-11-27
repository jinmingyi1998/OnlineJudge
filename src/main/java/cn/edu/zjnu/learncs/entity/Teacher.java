package cn.edu.zjnu.learncs.entity;

import javax.persistence.*;

@Entity
public class Teacher {
    public Teacher(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @OneToOne
    private User user;
    @Column(nullable = false,length = 255)
    private String job;
    @Column(columnDefinition = "LONGTEXT")
    private String introduction;
    @Column(columnDefinition = "BIT default 0")
    private Boolean active;
    @Column(nullable = false,columnDefinition = "BIGINT default 0")
    private Long score;
}
