package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.entity.oj.TeamApply;
import cn.edu.zjnu.acm.entity.oj.Teammate;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.TeamApplyRepository;
import cn.edu.zjnu.acm.service.ContestService;
import cn.edu.zjnu.acm.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api/team")
public class TeamController {
    public static final int PAGE_SIZE = 120;
    @Autowired
    private TeamService teamService;

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

    @Autowired
    HttpSession session;
    @Autowired
    TeamApplyRepository teamApplyRepository;

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
        teamApplyRepository.save(teamApply);
        return "success";
    }

    @PostMapping("/apply/approve/{id:[0-9]+}")
    public String applyApproveTeam(@PathVariable(value = "id") Long id) {
        TeamApply teamApply = teamService.resolveApply(id, true);
        if (teamApply == null)
            throw new NotFoundException();
        return "success";
    }

    @PostMapping("/apply/reject/{id:[0-9]+}")
    public String applyRejectTeam(@PathVariable(value = "id") Long id) {
        TeamApply teamApply = teamService.resolveApply(id, false);
        if (teamApply == null)
            throw new NotFoundException();
        return "success";
    }

    @Autowired
    ContestService contestService;

    @GetMapping("/{id:[0-9]+}")
    public Team teamIndex(@PathVariable(value = "id") Long id) {
        Team team = teamService.getTeamById(id);
        if (team==null)
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
    public String teamDetail() {
        return "team/teaminfo";
    }
}
