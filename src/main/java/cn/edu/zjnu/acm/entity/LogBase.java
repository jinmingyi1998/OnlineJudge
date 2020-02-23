package cn.edu.zjnu.acm.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.time.Instant;

public class LogBase {
    public void saveLog(JpaRepository repository) {
        repository.save(this);
    }
}
