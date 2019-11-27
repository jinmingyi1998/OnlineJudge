/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.entity.oj;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

enum Visibility {
    pri("private"), pub("public");
    String name;

    Visibility(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }
}

enum JoinPolicy {
    invite("invite"), apply("apply"), free("free");
    String name;

    JoinPolicy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Slf4j
@Data
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255, unique = true)
    private String name;
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Contest> contests;
    @Column(nullable = false)
    private Visibility visibility;
    @Column(nullable = false)
    private JoinPolicy joinPolicy;
    @Column(columnDefinition = "LONGTEXT")
    private String introduction;
    @Column
    private Instant createTime;

    public String getNormalCreateTime(){
        return new SimpleDateFormat("yyyy-MM-dd").format(Date.from(createTime));
    }
    @JsonBackReference
    @OneToMany(mappedBy = "team")
    private List<TeamRole> teamRoles;

    protected Team() {
        name = "";
        introduction = "";
        joinPolicy = JoinPolicy.free;
        contests = new ArrayList<>();
        visibility = Visibility.pub;
        createTime = Instant.now();
        teamRoles = new ArrayList<>();
    }

    public Team(String name, List<Contest> contests, Visibility visibility, JoinPolicy joinPolicy, String introduction, Instant createTime, List<TeamRole> teamRoles) {
        this.name = name;
        this.contests = contests;
        this.visibility = visibility;
        this.joinPolicy = joinPolicy;
        this.introduction = introduction;
        this.createTime = createTime;
    }

    public Team(String name, JoinPolicy joinPolicy, String introduction) {
        this.name = name;
        this.joinPolicy = joinPolicy;
        this.introduction = introduction;
        contests = new ArrayList<>();
        visibility = Visibility.pub;
        createTime = Instant.now();
        teamRoles = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contests=" + contests +
                ", visibility=" + visibility +
                ", joinPolicy=" + joinPolicy +
                ", introduction='" + introduction + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public Boolean validator() {
        return this.name.length() > 0 &&
                this.name.length() < 50 &&
                this.introduction.length() < 2000;
    }
}


