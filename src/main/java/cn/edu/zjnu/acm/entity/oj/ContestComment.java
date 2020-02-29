package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContestComment extends Comment {
    @ManyToOne(optional = false)
    private Contest contest;

    public ContestComment() {
    }

    public ContestComment(User user, String text, Comment father, Contest contest) {
        super(user, text, father);
        this.contest = contest;
    }
}
