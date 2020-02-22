package cn.edu.zjnu.acm.entity.oj;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@Entity
@Slf4j
public class ContestComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Contest contest;
    @OneToOne(optional = false)
    private Comment comment;

    public ContestComment() {
    }
}
