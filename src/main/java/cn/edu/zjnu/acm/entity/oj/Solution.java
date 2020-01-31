package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

@Data
@Log4j2
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Solution implements Cloneable {
    @JsonIgnore
    public static final String AC = "Accepted";
    @JsonIgnore
    public static final String WA = "Wrong Answer";
    @JsonIgnore
    public static final String PENDING = "Pending";
    @JsonIgnore
    public static final String TLE = "Time Limit Exceeded";
    @JsonIgnore
    public static final String MLE = "Memory Limit Exceeded";
    @JsonIgnore
    public static final String RE = "Runtime Error";
    @JsonIgnore
    public static final String CE = "Compile Error";
    @JsonIgnore
    public static final String SE = "System Error";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User user;
    @ManyToOne(optional = false)
    private Problem problem;
    /**
     * C: c
     * C++: cpp
     * Java: java
     * Python2: py2
     * Python3: py3
     */
    @Column(nullable = false, columnDefinition = "varchar(40) default 'c'")
    private String language;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String source;
    @Column(nullable = false)
    private Instant submitTime;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false, columnDefinition = "integer default -1")
    private Integer time;
    @Column(nullable = false, columnDefinition = "integer default -1")
    private Integer memory;
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer length;
    @Column(nullable = false, columnDefinition = "varchar(50) default 'Wrong Answer'", length = 50)
    private String result = WA;
    @Column(nullable = false, columnDefinition = "bit(1) default 0")
    private Boolean share;
    @Column(nullable = false, columnDefinition = "LONGTEXT default ''")
    private String info;
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer caseNumber = 0;
    @JsonIgnore
    @ManyToOne
    private Contest contest = null;

    public Solution() {
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
        result = PENDING;
        memory = 0;
        time = 0;
        info = "";
        contest = null;
    }

    public String getNormalResult() {
        if (result.equals(Solution.AC)) return result;
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

    public Solution clone() throws CloneNotSupportedException {
        Solution s = (Solution) super.clone();
        s.setProblem(problem.clone());
        s.setUser(user.clone());
        s.setContest(contest.clone());
        return s;
    }
}
