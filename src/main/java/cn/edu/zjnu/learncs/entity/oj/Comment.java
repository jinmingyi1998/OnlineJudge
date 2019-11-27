/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.entity.oj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jinmy.onlinejudge.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long fatherId;
    @Column(nullable = false)
    private Instant postTime;
    @ManyToOne
    private User user;
    @Column(columnDefinition = "LONGTEXT")
    private String text;
    @JsonIgnore
    @ManyToOne
    private Contest contest;

    public Comment(User user, String text, Contest contest) {
        this.user = user;
        this.text = text;
        this.contest = contest;
        postTime=Instant.now();
    }

    public Comment() {
    }

    @JsonManagedReference
    public String getNormalPostTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(postTime));
    }
}



