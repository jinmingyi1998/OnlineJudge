package cn.edu.zjnu.acm.entity.oj;

import cn.edu.zjnu.acm.entity.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Data
public class AnalysisComment extends Comment {

    @ManyToOne(optional = false)
    private Analysis analysis;

    public AnalysisComment() {
    }

    public AnalysisComment(User user, String text, Comment father, Analysis analysis) {
        super(user, text, father);
        this.analysis = analysis;
    }
}
