package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Contest implements Cloneable {
    public static final String PUBLIC = "public";
    public static final String TEAM = "team";
    public static final String PRIVATE = "private";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO check contest title unique when creating
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, columnDefinition = "varchar(20) default 'public'")
    private String privilege = PUBLIC;
    @Column(length = 200)
    private String password = "";
    @Column(nullable = false)
    private Instant startTime;
    @Column(nullable = false)
    private Instant endTime;
    @ManyToOne(optional = false)
    private User creator;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant createTime;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.REMOVE)
    private List<ContestComment> contestComments;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.REMOVE)
    private List<ContestProblem> problems;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.REMOVE)
    private List<Solution> solutions;
    @Column(nullable = false, columnDefinition = "varchar(20) default 'acm'")
    private String pattern = "acm";
    @Column(nullable = false, columnDefinition = "bit(1) default 1")
    private Boolean freezeRank = Boolean.TRUE;
    @ManyToOne
    private Team team;

    public Contest(String title,
                   String description,
                   String privilege,
                   String password,
                   Instant startTime,
                   Instant endTime,
                   Instant createTime) {
        this.title = title;
        this.description = description;
        this.privilege = privilege;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.pattern = "acm";
        this.freezeRank = false;
    }

    public Contest() {
    }

    /**
     * @return true if contest is ended, false otherwise.
     */
    public Boolean isEnded() {
        return Instant.now().compareTo(endTime) > 0;
    }

    public Boolean isStarted() {
        return Instant.now().compareTo(startTime) > 0;
    }

    public String getRunStatu() {
        if (Instant.now().compareTo(endTime) > 0)
            return "已结束";
        else if (Instant.now().compareTo(startTime) > 0)
            return "进行中";
        else return "未开始";
    }

    public String getLength() {
        Duration d = Duration.between(startTime, endTime);
        Long minutes = d.toMinutes();
        return minutes.toString();
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

    public void setStartAndEndTime(String startTime, Long length) {
        setStartTime(startTime);
        this.endTime = this.startTime.plusSeconds(60 * length);
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

    @Override
    public String toString() {
        return "Contest{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", privilege='" + privilege + '\'' +
                ", password='" + password + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", creator=" + creator +
                ", createTime=" + createTime +
                ", contestComments=" + contestComments +
                ", problems=" + problems +
                ", solutions=" + solutions +
                ", pattern='" + pattern + '\'' +
                ", freezeRank=" + freezeRank +
                '}';
    }

    public Contest clone() throws CloneNotSupportedException {
        Contest c = (Contest) super.clone();
        c.setCreator(creator.clone());
        return c;
    }

    public Contest clearLazyRoles() {
        setSolutions(null);
        setProblems(null);
        setContestComments(null);
        if (team != null) {
            team.clearLazyRoles();
        }
        return this;
    }

    public Contest hideInfo() {
        setCreator(null);
        setPattern(null);
        setPassword(null);
        setPrivilege(null);
        setFreezeRank(null);
        return this;
    }
}