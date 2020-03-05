package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.exception.ForbiddenException;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ContestService;
import cn.edu.zjnu.acm.service.TeamService;
import cn.edu.zjnu.acm.service.UserService;
import cn.edu.zjnu.acm.util.RestfulResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/team")
public class TeamController {
    private static final int PAGE_SIZE = 120;
    private final TeamService teamService;
    private final UserService userService;
    private final ContestService contestService;
    private final HttpSession session;

    public TeamController(TeamService teamService, HttpSession session, UserService userService, ContestService contestService) {
        this.teamService = teamService;
        this.session = session;
        this.userService = userService;
        this.contestService = contestService;
    }

    @GetMapping("")
    public RestfulResult teamPage(@RequestParam(value = "page", defaultValue = "0") int page) {
        page = Math.max(0, page);
        Page<Team> return_page = teamService.getAll(page, PAGE_SIZE);
        for (Team t : return_page.getContent()) {
            t.clearLazyRoles();
            t = teamService.fillTeamTeammate(t);
            t.hideInfo();
        }
        return new RestfulResult(200, RestfulResult.SUCCESS, return_page);
    }

    @GetMapping("/myteams")
    public List<Team> getMyTeam(@SessionAttribute User currentUser) {
        if (currentUser == null) {
            return null;
        }
        return teamService.teamsOfUser(currentUser);
    }

    private boolean isUserPermitted(Long tid, Integer require_level) {
        Team team = teamService.getTeamById(tid);
        if (team == null)
            throw new NotFoundException();
        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            throw new ForbiddenException();
        Teammate teammate = teamService.getUserInTeam(user, team);
        if (teammate.getLevel() > require_level) {
            throw new ForbiddenException();
        }
        return true;
    }

    @GetMapping("/showapply/{teamid:[0-9]+}")
    public List<TeamApply> showApply(@PathVariable(value = "teamid") Long teamid) {
        if (!isUserPermitted(teamid, Teammate.MANAGER))
            throw new ForbiddenException();
        Team team = teamService.getTeamById(teamid);
        List<TeamApply> teamApplies = teamService.getAllApplies(team);
        for (TeamApply t : teamApplies) {
            t.setTeam(null);
            t.getUser().hideInfo();
        }
        teamApplies.sort((o1, o2) -> (int) (o1.getId() - o2.getId()) * -1);
        return teamApplies;
    }

    @DeleteMapping("/delete/teammate/{id:[0-9]+}/{teamid:[0-9]+}")
    public String deleteTeammate(@PathVariable(value = "id") Long id) {
        Teammate teammate = teamService.getTeammateById(id);
        if (teammate == null) {
            throw new NotFoundException();
        }
        if (!isUserPermitted(teammate.getTeam().getId(), Teammate.MANAGER)) {
            return "Permission denied";
        }
        User user = (User) session.getAttribute("currentUser");
        Teammate selfTeammate = teamService.getUserInTeam(user, teammate.getTeam());
        if (selfTeammate.getLevel() >= teammate.getLevel())
            throw new ForbiddenException();
        if (teammate.getUser().getId() == user.getId())
            return "failed";
        teamService.deleteTeammate(teammate);
        return "success";
    }

    private void updateTeammateLevel(Long tid, Integer level) {
        Teammate teammate = teamService.getTeammateById(tid);
        if (teammate == null) {
            throw new NotFoundException();
        }
        if (!isUserPermitted(teammate.getTeam().getId(), Teammate.MASTER)) {
            throw new ForbiddenException();
        }
        teammate.setLevel(level);
        teamService.updateTeammate(teammate);
    }

    @PostMapping("/add/manager/{id:[0-9]+}/{teamid:[0-9]+}")
    public String addManager(@PathVariable(value = "id") Long tid) {
        updateTeammateLevel(tid, Teammate.MANAGER);
        return "success";
    }

    @PostMapping("/remove/manager/{id:[0-9]+}/{teamid:[0-9]+}")
    public String removeManager(@PathVariable(value = "id") Long id) {
        updateTeammateLevel(id, Teammate.MEMBER);
        return "success";
    }

    @PostMapping("/apply/{teamid:[0-9]+}")
    public String applyTeam(@PathVariable(value = "teamid") Long tid) {
        Team team = teamService.getTeamById(tid);
        if (team == null) {
            throw new NotFoundException();
        }
        if (team.getAttend().equals(Team.PRIVATE)) {
            return "failed";
        }
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new NeedLoginException();
        }
        if (teamService.isUserInTeam(user, team) || teamService.isUserHasApplied(user, team))
            return "already in team or has applied!";
        TeamApply teamApply = new TeamApply(user, team);
        teamApply = teamService.applyTeam(teamApply);
        return "success";
    }

    private TeamApply checkTeamApplyById(Long id) {
        TeamApply teamApply = teamService.getTeamApplyById(id);
        if (teamApply == null)
            throw new NotFoundException();
        if (!isUserPermitted(teamApply.getTeam().getId(), Teammate.MANAGER))
            throw new ForbiddenException();
        return teamApply;
    }

    @PostMapping("/apply/approve/{applyid:[0-9]+}/{teamid:[0-9]+}")
    public String applyApproveTeam(@PathVariable(value = "applyid") Long id) {
        TeamApply teamApply = checkTeamApplyById(id);
        teamService.resolveApply(teamApply, true);
        return "success";
    }

    @PostMapping("/apply/reject/{id:[0-9]+}/{teamid:[0-9]+}")
    public String applyRejectTeam(@PathVariable(value = "id") Long id) {
        TeamApply teamApply = checkTeamApplyById(id);
        teamService.resolveApply(teamApply, false);
        return "success";
    }

    @GetMapping("/{teamid:[0-9]+}")
    public Team teamIndex(@PathVariable(value = "teamid") Long id) {
        Team team = teamService.getTeamById(id);
        if (team == null)
            throw new NotFoundException();
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !teamService.isUserInTeam(user, team))
            throw new NotFoundException();
        team.setContests(contestService.contestsOfTeam(team));
        team = teamService.fillTeamTeammate(team);
        team.hideInfo();
        return team;
    }

    private String generateCode(int number) {
        int offset = (int) ((Math.random() * 10) % 10);
        StringBuffer sub = new StringBuffer();
        sub.append((char) (offset + 65));
        for (int i = 1; i < 18; i++) {
            char c = (char) (Math.random() * 26 + 65);
            sub.append(c);
        }
        String str = String.format("%06d", number);
        for (int i = 1; i <= 6; i++)
            sub.setCharAt(offset + i, (char) (str.charAt(i - 1) + 17 + offset));
        return String.valueOf(sub);
    }

    private int decode(String s) {
        int offset = s.charAt(0) - 65;
        int result = 0;
        for (int i = 1; i <= 6; i++) {
            result *= 10;
            int n = s.charAt(offset + i) - 65 - offset;
            result += n;
        }
        return result;
    }

    @GetMapping("/invite/{teamid:[0-9]+}")
    public String getInviteLink(@PathVariable(value = "teamid") Long id) {
        Team team = teamService.getTeamById(id);
        if (team == null)
            throw new NotFoundException();
        return generateCode(id.intValue());
    }

    @GetMapping("/invite/{code:[A-Z]{18}}")
    public String doInviteLink(@PathVariable(value = "code") String code) {
        Long tid = Long.valueOf(decode(code));
        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            throw new NeedLoginException();
        Team team = teamService.getTeamById(tid);
        if (team == null)
            throw new NotFoundException();
        teamService.addTeammate(user, team);
        return "success";
    }

    @GetMapping("/update/attend/{teamid:[0-9]+}")
    public String updateTeamAttendStrategy(@PathVariable(value = "teamid") Long teamid,
                                           @RequestParam(value = "attend") String attend) {
        if (isUserPermitted(teamid, Teammate.MASTER)) {
            try {
                assert attend.equals(Team.PRIVATE) || attend.equals(Team.PUBLIC);
                Team team = teamService.getTeamById(teamid);
                teamService.updateTeamAttend(attend, team);
            } catch (AssertionError | NullPointerException ne) {
                throw new NotFoundException();
            }
        }
        return "success";
    }

    @PostMapping("/create")
    public String createTeam(@SessionAttribute User currentUser, @Validated @RequestBody Team team) {
        if (currentUser == null) {
            throw new NeedLoginException();
        }
        if (userService.getUserPermission(currentUser) == -1) {
            return "permission denied";
        }
        try {
            currentUser = userService.getUserById(currentUser.getId());
        } catch (NullPointerException e) {
            return "failed";
        }
        if (teamService.isTeamNameExist(team.getName())) {
            return "name existed!";
        }
        if (teamService.checkUserCreateTeamLimit(20, currentUser)) {
            return "number limit exceed";
        }
        team.setCreator(currentUser);
        team.setTeammates(new ArrayList<Teammate>());
        team.setContests(new ArrayList<Contest>());
        team.setCreateTime(Instant.now());
        team = teamService.addTeam(team);
        teamService.addTeammate(currentUser, team, Teammate.MASTER);
        return "success";
    }

    @GetMapping("/leave/{teamid:[0-9]+}")
    public String leaveTeam(@PathVariable("teamid") Long teamId) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            throw new NeedLoginException();
        }
        Team team = teamService.getTeamById(teamId);
        if (!teamService.isUserInTeam(user, team)) {
            throw new NotFoundException();
        }
        Teammate teammate = teamService.getUserInTeam(user, team);
        if (teammate.getLevel() == Teammate.MASTER) {
            teamService.deleteTeam(team);
        } else {
            teamService.deleteTeammate(teammate);
        }
        return "success";
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

    @GetMapping("/create")
    public String teamTeam() {
        return "team/team_create";
    }
}
