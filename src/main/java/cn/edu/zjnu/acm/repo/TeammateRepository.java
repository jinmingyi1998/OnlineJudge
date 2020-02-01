package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeammateRepository extends JpaRepository<Teammate, Long> {
    List<Teammate> findAllByTeam(Team team);

    List<Teammate> findByUser(User user);

    Optional <Teammate> findByUserAndTeam(User user,Team team);

}
