package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.LazyInitializationException;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Team {
    //不能叫Group  会让sql误会
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 1, max = 99)
    @Column(nullable = false, unique = true, columnDefinition = "varchar(100) default ''")
    private String name;
    @Column(nullable = false, columnDefinition = "LONGTEXT default ''")
    private String description;
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<Teammate> teammates;
    @ManyToOne(optional = false)
    private User creator;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team",cascade = CascadeType.REMOVE)
    private List<Contest> contests;
    @Column(nullable = false, columnDefinition = "varchar(50) default 'public'")
    private String attend = PUBLIC;
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant createTime;

    public String getNormalCreateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date.from(createTime));
    }

    public Team clearLazyRoles() {
        setTeammates(null);
        setContests(null);
        return this;
    }

    public Team hideInfo() {
        creator.hideInfo();
        try {
            if (contests != null)
                for (Contest c : contests) {
                    c.setTeam(null);
                    c.clearLazyRoles();
                    c.hideInfo();
                }
            if (teammates != null)
                for (Teammate t : teammates) {
                    t.getUser().hideInfo();
                    t.setTeam(null);
                }
        } catch (LazyInitializationException e) {
            ;
        }
        return this;
    }
}
