package cn.edu.zjnu.learncs.entity.oj;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Log4j2
@Data
@Entity

public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, unique = true, nullable = false)
    private String title;
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;
    @Column(columnDefinition = "LONGTEXT")
    private String input;
    @Column(columnDefinition = "LONGTEXT")
    private String output;
    @Column(columnDefinition = "LONGTEXT")
    private String sampleInput;
    @Column(columnDefinition = "LONGTEXT")
    private String sampleOutput;
    @Column(columnDefinition = "LONGTEXT")
    private String hint;
    @Column(columnDefinition = "LONGTEXT")
    private String source;
    @Column(nullable = false, columnDefinition = "int default 1000")
    private Integer timeLimit;
    @Column(nullable = false, columnDefinition = "int default 65536")
    private Integer memoryLimit;
    @Column(nullable = false)
    private Boolean active = false;
    @Column(columnDefinition = "integer default 0")
    private Integer score;

//    @ManyToMany(fetch = FetchType.EAGER)
//    private List<Tag> tags;
//    @JsonBackReference
//    @OneToMany(mappedBy = "problem")
//    private List<Solution> solutions = new ArrayList<>();
    @Column(columnDefinition = "integer default 0")
    private Integer submit = 0;
    @Column(columnDefinition = "integer default 0")
    private Integer accepted = 0;
//    public String tagsToString(){
//        String str="";
//        for (int i = 0; i < tags.size(); i++) {
//            str+=","+tags.get(i).getName();
//        }
//        return str.substring(1);
//    }

//    public Problem() {
//        solutions = null;
//    }

    public Problem(String title, String description, String input, String output, String sampleInput, String sampleOutput, String hint, String source, Integer timeLimit, Integer memoryLimit, Integer score) {
        this.title = title;
        this.description = description;
        this.input = input;
        this.output = output;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.hint = hint;
        this.source = source;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        accepted = 0;
        submit = 0;
        active = false;
        this.score = score;
    }


    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", sampleInput='" + sampleInput + '\'' +
                ", sampleOutput='" + sampleOutput + '\'' +
                ", hint='" + hint + '\'' +
                ", source='" + source + '\'' +
                ", timeLimit=" + timeLimit +
                ", memoryLimit=" + memoryLimit +
                ", active=" + active +
                ", submit=" + submit +
                ", accepted=" + accepted +
                '}';
    }

    public String getRatio() {
        if (this.submit == 0) return "0%";
        double ratio=this.accepted * 1.0 / this.submit * 100.0;
        return  String.format("%.2f%%",ratio);
    }
    public int hashCode(){
        return this.id.hashCode();
    }

}
