package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class QuestionTag {
    public QuestionTag(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
}
