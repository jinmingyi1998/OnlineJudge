package cn.edu.zjnu.learncs.entity.learn;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PushInformation {
    public PushInformation(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 255)
    private String name;
    @Column(columnDefinition = "LONGTEXT",nullable = false)
    private String content;
    @Column(nullable = false)
    private String pictureUrl;
    @Column(nullable = false,length = 1023)
    private String introduction;
}
