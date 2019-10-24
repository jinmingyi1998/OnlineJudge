package cn.edu.zjnu.learncs.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Entity
@Slf4j
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 30)
    @NotEmpty
    @Size(min = 6, max = 30)
    private String username;

    @Column(nullable = false, length = 30)
    @NotEmpty
    @Size(min = 6, max = 30)
    private String password;

    @Column(nullable = false, length = 30)
    @NotEmpty
    @Size(min = 1, max = 30)
    private String name;

    @Column(length = 200)
    @Size(min = 4, max = 200)
    private String email;

    @Column(length = 255)
    @Size(max = 250)
    private String intro;

}
