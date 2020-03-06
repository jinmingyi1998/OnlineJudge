package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"problem_id", "user_id"})})
public class UserProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User user;
    @ManyToOne(optional = false)
    private Problem problem;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant createTime = Instant.now();

    public UserProblem() {
    }

    public UserProblem(User user, Problem problem) {
        this.user = user;
        this.problem = problem;
    }
}
