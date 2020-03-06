package cn.edu.zjnu.acm.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
public class ImageLog extends LogBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private User user;
    @Column(nullable = false, updatable = false)
    private String address;
    @Column(nullable = false, updatable = false)
    private String filename;
    @Column(nullable = false, updatable = false)
    private Long filesize;
    @Column(nullable = false, length = 255, updatable = false)
    private String url;
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Instant logTime;

    public ImageLog() {
    }

    public ImageLog(User user, String address, String filename, Long filesize, String url, Instant logTime) {
        this.user = user;
        this.address = address;
        this.filename = filename;
        this.filesize = filesize;
        this.url = url;
        this.logTime = logTime;
    }
}
