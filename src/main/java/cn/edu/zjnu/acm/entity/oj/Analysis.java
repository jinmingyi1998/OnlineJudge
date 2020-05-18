package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Entity
@Data
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 15, max = 5000)
    @Column(nullable = false)
    private String text;
    @ManyToOne(optional = false)
    private User user;
    @JsonIgnore
    @ManyToOne(optional = false)
    private Problem problem;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant postTime = Instant.now();
    @OneToMany(mappedBy = "analysis", cascade = CascadeType.REMOVE)
    private List<AnalysisComment> comment;

    public Analysis() {
    }

    public Analysis(@Size(min = 15, max = 5000) String text, User user, Problem problem, Instant postTime, List<AnalysisComment> comment) {
        this.text = text;
        this.user = user;
        this.problem = problem;
        this.postTime = postTime;
        this.comment = comment;
    }
}
