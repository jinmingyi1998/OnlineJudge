package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamApplyRepository extends JpaRepository<TeamApply, Long> {
    Optional<TeamApply> findById(Long id);

    List<TeamApply> findAllByTeam(Team team);

    Optional<TeamApply> findByUserAndTeamAndActive(User user, Team team, Boolean active);
}
