package cn.edu.zjnu.acm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

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
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false, columnDefinition = "INTEGER default 0")
    private Integer privilege = TEACHER;

    public Teacher() {
    }

    public Teacher(User user, Integer privilege) {
        this.user = user;
        this.privilege = privilege;
    }
}
