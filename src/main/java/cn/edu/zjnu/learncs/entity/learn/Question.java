package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 笔试题目
 */
@Entity
@Data
public class Question {
    public Question() {
    }

    public static final String CHOICE_QUESTION = "choice";
    public static final String FILLING_QUESTION = "fill";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,columnDefinition = "LONGTEXT")
    private String text;
    @Column(columnDefinition = "LONGTEXT")
    private String answer;
    @Column(columnDefinition = "LONGTEXT")
    private String interpretation;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false,columnDefinition = "BIGINT default 0")
    private Long score;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<QuestionTag> questionTagList;
}
