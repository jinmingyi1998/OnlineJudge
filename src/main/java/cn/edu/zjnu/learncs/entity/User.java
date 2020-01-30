package cn.edu.zjnu.learncs.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Entity
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Cloneable,Comparable{

    public User() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.name = "";
        this.intro = "";
    }


    public User(@NotEmpty @Size(min = 6, max = 30) String username, @NotEmpty @Size(min = 6, max = 30) String password, @NotEmpty @Size(min = 1, max = 30) String name, @Size(min = 4, max = 200) String email, @Size(max = 250) String intro) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.intro = intro;
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

    @Column(nullable = false, columnDefinition = "VARCHAR(250) default ''")
    @NotEmpty
    @Size(min = 1, max = 30)
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(250) default ''")
    @Size(min = 4, max = 200)
    private String email;

    @Column(nullable = false, columnDefinition = "VARCHAR(250) default ''")
    @Size(max = 250)
    private String intro;

    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return getId().compareTo(((User)o).getId());
    }
}
