package cn.edu.zjnu.learncs.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class UserProfile implements Serializable {
    public UserProfile() {
        score = 0;
        accepted = 0;
        submitted = 0;
    }

    public UserProfile(User u) {
        user = u;
        new UserProfile();
    }

    @Id
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    User user;
    @Column(nullable = false, columnDefinition = "INT default 0")
    Integer score = 0;
    @Column(nullable = false, columnDefinition = "INT default 0")
    Integer accepted = 0;
    @Column(nullable = false, columnDefinition = "INT default 0")
    Integer submitted = 0;
}
