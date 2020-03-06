package cn.edu.zjnu.acm.repo.team;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface TeammateRepository extends JpaRepository<Teammate, Long> {
    List<Teammate> findAllByTeam(Team team);

    List<Teammate> findByUser(User user);

    Optional<Teammate> findById(Long id);

    Optional<Teammate> findByUserAndTeam(User user, Team team);

    @Modifying
    void deleteById(Long id);

    @Modifying
    void deleteAllByTeam(Team team);
}
