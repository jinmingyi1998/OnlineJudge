package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.repo.contest.ContestRepository;
import cn.edu.zjnu.acm.repo.team.TeamApplyRepository;
import cn.edu.zjnu.acm.repo.team.TeamRepository;
import cn.edu.zjnu.acm.repo.team.TeammateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeammateRepository teammateRepository;
    private final TeamApplyRepository teamApplyRepository;
    private final ContestRepository contestRepository;

    public TeamService(TeamRepository teamRepository, TeammateRepository teammateRepository, TeamApplyRepository teamApplyRepository, ContestRepository contestRepository) {
        this.teamRepository = teamRepository;
        this.teammateRepository = teammateRepository;
        this.teamApplyRepository = teamApplyRepository;
        this.contestRepository = contestRepository;
    }

    public Page<Team> getAll(int page, int size) {
        return teamRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public void updateTeamAttend(String attend, Team team) {
        teamRepository.updateAttendById(attend, team.getId());
    }

    public Team fillTeamTeammate(Team team) {
        List<Teammate> teammateList = teammateRepository.findAllByTeam(team);
        for (Teammate t : teammateList) {
            t.setTeam(null);
        }
        team.setTeammates(teammateList);
        return team;
    }

    public List<Team> teamsOfUser(User user) {
        List<Teammate> teammates = teammateRepository.findByUser(user);
        List<Team> teams = new ArrayList<>();
        for (Teammate tm :
                teammates) {
            tm.getTeam().clearLazyRoles();
            tm.getTeam().hideInfo();
            teams.add(tm.getTeam());
        }
        return teams;
    }

    public Boolean isUserInTeam(User u, Team t) {
        Teammate teammate = teammateRepository.findByUserAndTeam(u, t).orElse(null);
        return teammate != null;
    }

    public Teammate getTeammateById(Long tid) {
        return teammateRepository.findById(tid).orElse(null);
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

    public Teammate addTeammate(User user, Team team, Integer... level) {
        if (getUserInTeam(user, team) == null) {
            Teammate teammate = new Teammate(user, team);
            if (level.length == 1)
                teammate.setLevel(level[0]);
            return teammateRepository.save(teammate);
        }
        return null;
    }

    public Boolean isTeamNameExist(String name) {
        return teamRepository.findByName(name).isPresent();
    }

    public Boolean checkUserCreateTeamLimit(int limit, User user) {
        return teamRepository.countAllByCreator(user) >= limit;
    }

    public TeamApply resolveApply(TeamApply teamApply, boolean approve) {
        teamApply.setResult(approve ? TeamApply.APPROVED : TeamApply.REJECTED);
        teamApply.setActive(false);
        teamApply = teamApplyRepository.save(teamApply);
        if (approve) {
            addTeammate(teamApply.getUser(), teamApply.getTeam());
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

    public Boolean isUserHasApplied(User user, Team team) {
        return teamApplyRepository.findByUserAndTeamAndActive(user, team, true).isPresent();
    }

    @Transactional
    public void deleteTeammate(Teammate teammate) {
        teammateRepository.deleteById(teammate.getId());
    }

    @Transactional
    public void deleteTeam(Team team) {
        teammateRepository.deleteAllByTeam(team);
        teamApplyRepository.deleteAllByTeam(team);
        teamRepository.delete(team);
    }
}
