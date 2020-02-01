package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.repo.TeamRepository;
import cn.edu.zjnu.acm.repo.TeammateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeammateRepository teammateRepository;

    public Page<Team> getAll(int page, int size) {
        return teamRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    public Team fillTeamTeammate(Team team) {
        List<Teammate> teammateList = teammateRepository.findAllByTeam(team);
        for (Teammate t : teammateList) {
            t.setTeam(null);
        }
        team.setTeammates(teammateList);
        return team;
    }

    public Boolean isUserInTeam(User u, Team t) {
        Teammate teammate = teammateRepository.findByUserAndTeam(u, t).orElse(null);
        return teammate != null;
    }

}
