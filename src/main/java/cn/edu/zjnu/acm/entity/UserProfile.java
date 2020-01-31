package cn.edu.zjnu.acm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, columnDefinition = "INT default 0")
    private Integer score = 0;
    @Column(nullable = false, columnDefinition = "INT default 0")
    private Integer accepted = 0;
    @Column(nullable = false, columnDefinition = "INT default 0")
    private Integer submitted = 0;
    public UserProfile() {
        score = 0;
        accepted = 0;
        submitted = 0;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", score=" + score +
                ", accepted=" + accepted +
                ", submitted=" + submitted +
                '}';
    }
}
