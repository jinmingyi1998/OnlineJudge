package cn.edu.zjnu.learncs.entity.oj;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Data
@Log4j2
public class CompileError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToOne
    private Solution solution;
    @Column(columnDefinition = "LONGTEXT")
    private String info;

    public CompileError() {
    }

    public CompileError(Solution solution, String info) {
        this.solution = solution;
        this.info = info;
    }

    @Override
    public String toString() {
        return "CompileError{" +
                "solution=" + solution +
                ", info='" + info + '\'' +
                '}';
    }
}
