package cn.edu.zjnu.learncs.entity.oj;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    String name;
    @Column
    String url;
    @Column
    String thumburl;
    @Column
    String tag;

    public Video() {
    }

    public Video(String name, String url, String tag) {
        this.name = name;
        this.url = url;
        this.tag = tag;
    }
}
