package cn.edu.zjnu.acm.entity;

import cn.edu.zjnu.acm.entity.oj.Contest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher {
    @JsonIgnore
    public static final Integer TEACHER = 0;
    @JsonIgnore
    public static final Integer ADMIN = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @OneToMany(mappedBy = "creator")
    List<Contest> contests;
    public Teacher(){}
}
