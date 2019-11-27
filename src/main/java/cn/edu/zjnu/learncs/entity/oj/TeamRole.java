/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.entity.oj;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jinmy.onlinejudge.entity.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@Slf4j
@Entity
public class TeamRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Team team;
    @ManyToOne
    private User user;
    private Role role;
    public TeamRole(Team team, User user, Role role) {
        this.team = team;
        this.user = user;
        this.role = role;
    }
    public TeamRole() {
    }

    @Override
    public String toString() {
        return "TeamRole{" +
                "id=" + id +
                ", team=" + team +
                ", user=" + user +
                ", role=" + role +
                '}';
    }

    public enum Role {
        admin("Administrator"), manager("Manager"), member("Member");
        String name;

        Role(String s) {
            name = s;
        }
    }
}
