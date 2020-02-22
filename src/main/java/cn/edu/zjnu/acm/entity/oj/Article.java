package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Slf4j
public class Article {
    public Article() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "LONGTEXT DEFAULT ''")
    private String text;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant postTime;
    @ManyToOne(optional = false)
    private User user;
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer up;
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer down;
    @OneToMany(mappedBy = "article")
    private List<ArticleComment> comment;
}
