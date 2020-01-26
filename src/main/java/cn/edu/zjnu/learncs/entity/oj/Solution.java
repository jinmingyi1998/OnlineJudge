package cn.edu.zjnu.learncs.entity.oj;

import cn.edu.zjnu.learncs.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@Log4j2
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
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
     * C++: cpp
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
    @Column(nullable = false, columnDefinition = "LONGTEXT default ''")
    private String info;
    @Column
    private Integer caseNumber = 0;

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
        result = "Pending";
        memory = 0;
        time = 0;
        info = "";
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
