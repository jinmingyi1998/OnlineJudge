package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ContestService;
import cn.edu.zjnu.acm.service.TeamService;
import cn.edu.zjnu.acm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/team")
public class TeamController {
    private static final int PAGE_SIZE = 120;
    private final TeamService teamService;
    private final
    HttpSession session;

    @GetMapping
    public Page<Team> teamPage(@RequestParam(value = "page", defaultValue = "0") int page) {
        page = Math.max(0, page);
        Page<Team> return_page = teamService.getAll(page, PAGE_SIZE);
        for (Team t : return_page.getContent()) {
            t.clearLazyRoles();
            t = teamService.fillTeamTeammate(t);
            t.hideInfo();
        }
        return return_page;
    }

    private final
    UserService userService;
    private final
    ContestService contestService;

    public TeamController(TeamService teamService, HttpSession session, UserService userService, ContestService contestService) {
        this.teamService = teamService;
        this.session = session;
        this.userService = userService;
        this.contestService = contestService;
    }

    private boolean isUserPermitted(Long tid) {
        Team team = teamService.getTeamById(tid);
        if (team == null)
            throw new NotFoundException();
        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            throw new ForbiddenException();
        Teammate teammate = teamService.getUserInTeam(user, team);
        if (teammate.getLevel().equals(Teammate.MEMBER)) {
            throw new ForbiddenException();
        }
        return true;
    }

    @GetMapping("/showapply/{gid:[0-9]+}")
    public List<TeamApply> showApply(@PathVariable(value = "gid") Long gid) {
        if (!isUserPermitted(gid))
            throw new ForbiddenException();
        Team team = teamService.getTeamById(gid);
        List<TeamApply> teamApplies = teamService.getAllApplies(team);
        for (TeamApply t : teamApplies) {
            t.setTeam(null);
            t.getUser().hideInfo();
        }
        return teamApplies;
    }

    private Teammate getTeammate(Long uid, Long tid) {
        User user = userService.getUserById(uid);
        Team team = teamService.getTeamById(tid);
        if (user == null || team == null)
            return null;
        return teamService.getUserInTeam(user, team);
    }

    @DeleteMapping("/delete/teammate/{uid:[0-9]+}/{tid:[0-9]+}")
    public String deleteTeammate(@PathVariable(value = "uid") Long uid,
                                 @PathVariable(value = "tid") Long tid) {
        if (!isUserPermitted(tid)) {
            throw new ForbiddenException();
        }
        Teammate teammate = getTeammate(uid, tid);
        if (teammate == null) {
            throw new NotFoundException();
        }
        teamService.deleteTeammate(teammate);
        return "success";
    }

    @PostMapping("/add/manager/{uid:[0-9]+}/{tid:[0-9]+}")
    public String addManager(@PathVariable(value = "uid") Long uid,
                             @PathVariable(value = "tid") Long tid) {
        if (!isUserPermitted(tid)) {
            throw new ForbiddenException();
        }
        Teammate teammate = getTeammate(uid, tid);
        if (teammate == null) {
            throw new NotFoundException();
        }
        teammate.setLevel(Teammate.MANAGER);
        teamService.updateTeammate(teammate);
        return "success";
    }

    @PostMapping("/apply/{gid:[0-9]+}")
    public String applyTeam(@PathVariable(value = "gid") Long tid) {
        Team team = teamService.getTeamById(tid);
        if (team == null) {
            throw new NotFoundException();
        }
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new NeedLoginException();
        }
        TeamApply teamApply = new TeamApply(user, team);
        teamService.applyTeam(teamApply);
        return "success";
    }

    @PostMapping("/apply/approve/{id:[0-9]+}")
    public String applyApproveTeam(@PathVariable(value = "id") Long id) {
        TeamApply teamApply = teamService.getTeamApplyById(id);
        if (teamApply == null)
            throw new NotFoundException();
        if (!isUserPermitted(teamApply.getTeam().getId()))
            throw new ForbiddenException();
        teamService.resolveApply(teamApply, true);
        return "success";
    }

    @PostMapping("/apply/reject/{id:[0-9]+}")
    public String applyRejectTeam(@PathVariable(value = "id") Long id) {
        TeamApply teamApply = teamService.getTeamApplyById(id);
        if (teamApply == null)
            throw new NotFoundException();
        if (!isUserPermitted(teamApply.getTeam().getId()))
            throw new ForbiddenException();
        teamService.resolveApply(teamApply, false);
        return "success";
    }

    @GetMapping("/{id:[0-9]+}")
    public Team teamIndex(@PathVariable(value = "id") Long id) {
        Team team = teamService.getTeamById(id);
        if (team == null)
            throw new NotFoundException();
        team.setContests(contestService.contestsOfTeam(team));
        team = teamService.fillTeamTeammate(team);
        team.hideInfo();
        return team;
    }

}

@Controller
@RequestMapping("/team")
class TeamViewController {
    @GetMapping
    public String showGroups() {
        return "team/teamindex";
    }

    @GetMapping("/{id:[0-9]+}")
    public String teamDetail(@PathVariable(value = "id") Long id) {
        return "team/teaminfo";
    }

    @GetMapping("/manage/{id:[0-9]+}")
    public String teamManage(@PathVariable(value = "id") Long id) {
        return "team/team_manage";
    }
}
