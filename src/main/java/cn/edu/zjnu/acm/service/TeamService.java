package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.repo.TeamApplyRepository;
import cn.edu.zjnu.acm.repo.TeamRepository;
import cn.edu.zjnu.acm.repo.TeammateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final
    TeamRepository teamRepository;
    private final
    TeammateRepository teammateRepository;
    private final
    TeamApplyRepository teamApplyRepository;

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

    public TeamService(TeamRepository teamRepository, TeammateRepository teammateRepository, TeamApplyRepository teamApplyRepository) {
        this.teamRepository = teamRepository;
        this.teammateRepository = teammateRepository;
        this.teamApplyRepository = teamApplyRepository;
    }

    public Teammate getUserInTeam(User u, Team t) {
        return teammateRepository.findByUserAndTeam(u, t).orElse(null);
    }

    public Team getTeamById(Long tid) {
        return teamRepository.findById(tid).orElse(null);
    }

    public void updateTeammate(Teammate teammate) {
        teammateRepository.save(teammate);
    }

    public TeamApply resolveApply(TeamApply teamApply, boolean approve) {
        teamApply.setResult(approve ? TeamApply.APPROVED : TeamApply.REJECTED);
        teamApply.setActive(false);
        if (approve) {
            Teammate teammate = new Teammate(teamApply.getUser(), teamApply.getTeam());
            teammateRepository.save(teammate);
        }
        return teamApply;
    }

    public List<TeamApply> getAllApplies(Team team) {
        return teamApplyRepository.findAllByTeam(team);
    }

    public TeamApply getTeamApplyById(Long id) {
        return teamApplyRepository.findById(id).orElse(null);
    }

    public TeamApply applyTeam(TeamApply teamApply) {
        return teamApplyRepository.save(teamApply);
    }

    public void deleteTeammate(Teammate teammate) {
        teammateRepository.deleteById(teammate.getId());
    }
}
