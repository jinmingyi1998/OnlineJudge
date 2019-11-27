/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.entity.oj;

import com.jinmy.onlinejudge.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@Entity
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 255)
    private String description;
    @Column(nullable = false, columnDefinition = "varchar(20) default 'public'")
    private String privilege;
    @Column(length = 200)
    private String password;
    @Column(nullable = false)
    private Instant startTime;
    @Column(nullable = false)
    private Instant endTime;
    @ManyToOne
    private User creator;
    @Column(nullable = false)
    private Instant createTime;
    @OneToMany(mappedBy = "contest")
    private List<Comment> contestComments;
    @OneToMany(mappedBy = "contest")
    private List<ContestProblem> problems;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solution> solutions;
    @Column(nullable = false, columnDefinition = "varchar(20) default 'acm'")
    private String pattern;

    public Boolean isEnded() {
        return Instant.now().compareTo(endTime) > 0;
    }

    public String getRunStatu() {
        if (Instant.now().compareTo(endTime) > 0)
            return "已结束";
        else if (Instant.now().compareTo(startTime) > 0)
            return "进行中";
        else return "未开始";
    }
    public String getLength(){
        Duration d=Duration.between(startTime,endTime);
        Long minutes=d.toMinutes();
        return (minutes/60)+":"+(minutes%60);
    }

    public Contest(String title, String description, String privilege, String password, Instant startTime, Instant endTime, Instant createTime, List<ContestProblem> contestProblems, List<Comment> contestComments) {
        this.title = title;
        this.description = description;
        this.privilege = privilege;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.pattern = "acm";
    }

    public String getNormalStartTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date.from(startTime));
    }

    public String getNormalEndTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date.from(endTime));
    }

    public void setStartTime(String startTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(startTime, dtf);
        this.startTime = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
    }

    public void setEndTime(String startTime, String lastTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(startTime, dtf);
        this.startTime = Instant.from(localDateTime.atZone(ZoneId.systemDefault()));
        String ps = "PT" + lastTime.split(":")[0] + "H" + lastTime.split(":")[1] + "M";
        System.out.println(ps);
        Duration duration = Duration.parse(ps);
        this.endTime = this.startTime.plus(duration);
    }

    public Contest() {
    }

    @Override
    public String toString() {
        return "Contest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", privilege='" + privilege + '\'' +
                ", password='" + password + '\'' +
                ", startTime=" + startTime +
                ", lastTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}