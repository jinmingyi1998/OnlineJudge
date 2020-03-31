package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
public class TeamApply implements Serializable {
    public static final String REJECTED = "rejected";
    public static final String APPROVED = "approved";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Team team;
    @ManyToOne(optional = false)
    private User user;
    @Column(columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Instant time;
    @Column(nullable = false, columnDefinition = "bit(1) default 1")
    private Boolean active = Boolean.TRUE;
    @Column(nullable = false, columnDefinition = "varchar(20) default 'rejected'")
    private String result = REJECTED;

    public TeamApply() {
    }

    public TeamApply(User u, Team t) {
        team = t;
        user = u;
        result = REJECTED;
        active = true;
        time = Instant.now();
    }
}
