package cn.edu.zjnu.learncs.entity.oj;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jinmy.onlinejudge.entity.User;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@Log4j2
@Entity
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Problem problem;
    /**
     * C: c
     * C++11: cpp
     * Java: java
     * Python2: py2
     * Python3: py3
     */
    @Column(nullable = false)
    private String language;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String source;
    @Column(nullable = false)
    private Instant submitTime;
    @Column(nullable = false)
    private String ip;
    @JsonIgnore
    @ManyToOne
    private Contest contest;
    @Column(nullable = false, columnDefinition = "integer default -1")
    private Integer time;
    @Column(nullable = false, columnDefinition = "integer default -1")
    private Integer memory;
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer length;
    @Column(nullable = false, columnDefinition = "varchar(50) default 'WA'", length = 50)
    private String result;
    @Column
    private Boolean share;
    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
    private CompileError ce;
    @Column
    private Integer caseNumber = 0;

    public Solution() {
        contest = null;
    }

    public Solution(User user, Problem problem, String language, String source, String ip, Boolean share) {
        this.user = user;
        this.problem = problem;
        this.language = language;
        this.source = source;
        this.ip = ip;
        this.share = share;
        length = source.length();
        submitTime = Instant.now();
        result = "Pending";
        memory = 0;
        time = 0;
        ce = null;
        contest = null;
    }

    public Solution(User user, Problem problem, String language, String source, Instant submitTime, String ip, Contest contest, Integer time, Integer memory, Integer length, String result, Boolean share, CompileError ce) {
        this.user = user;
        this.problem = problem;
        this.language = language;
        this.source = source;
        this.submitTime = submitTime;
        this.ip = ip;
        this.contest = contest;
        this.time = time;
        this.memory = memory;
        this.length = length;
        this.result = result;
        this.share = share;
        this.ce = ce;
    }

    public String getNormalResult() {
        if (result.equals("Accepted")) return result;
        return result + " on case " + caseNumber;
    }

    public String getNormalLanguage() {
        switch (language) {
            case "c":
                return "C";
            case "cpp":
                return "C++";
            case "java":
                return "Java";
            case "py2":
                return "Python2";
            case "py3":
                return "Python3";
        }
        return "";
    }

    public String getNormalSubmitTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(submitTime));
    }
}
