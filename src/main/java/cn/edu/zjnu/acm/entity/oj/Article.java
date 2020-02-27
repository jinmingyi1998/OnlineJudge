package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotNull(message = "title can't be null")
    @Size(min = 1,message = "title too short")
    private String title;
    @Size(min = 15,message = "text too short")
    @NotNull(message = "text cannot be null")
    @Column(nullable = false, columnDefinition = "LONGTEXT DEFAULT ''")
    private String text;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant postTime=Instant.now();
    @ManyToOne(optional = false)
    private User user;
    @OneToMany(mappedBy = "article",cascade = CascadeType.REMOVE)
    private List<ArticleComment> comment;
    public Article() {
    }
}
