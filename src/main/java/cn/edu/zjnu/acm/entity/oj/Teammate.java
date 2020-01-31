package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Teammate {
    public static final Integer MASTER = 0;
    public static final Integer MANAGER = 1;
    public static final Integer MEMBER = 2;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(optional = false)
    User user;
    @ManyToOne(optional = false)
    Team team;
    @Column(nullable = false, columnDefinition = "INT default 2")
    Integer level = MEMBER;
    public Teammate() {
    }

    public Teammate(User user, Team team) {
        this.user = user;
        this.team = team;
    }

    @Override
    public String toString() {
        return "Teammate{" +
                "id=" + id +
                ", user=" + user +
                ", level=" + level +
                '}';
    }
}