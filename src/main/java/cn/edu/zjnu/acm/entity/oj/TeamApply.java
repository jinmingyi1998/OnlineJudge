package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
public class TeamApply implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(optional = false)
    Team team;
    @ManyToOne(optional = false)
    User user;
    @Column(columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    Instant time;
    @Column(nullable = false,columnDefinition = "bit(1) default 1")
    Boolean active;
    @Column(nullable = false,columnDefinition = "varchar(20) default 'rejected'")
    String result;
    public TeamApply(){}
    public TeamApply(User u,Team t){
        team=t;
        user=u;
        time=Instant.now();
    }
}