package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Chapter {
    public Chapter(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 255)
    private String name;
    @Column(nullable = false,columnDefinition = "BIT default 0")
    private Boolean active;
    @OneToMany(fetch = FetchType.EAGER)
    List<Period> periodList;
}
