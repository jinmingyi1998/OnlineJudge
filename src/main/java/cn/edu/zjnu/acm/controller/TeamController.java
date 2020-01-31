package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    public static final int PAGE_SIZE = 120;
    @Autowired
    private TeamService teamService;

    @GetMapping
    public Page<Team> teamPage(@RequestParam(value = "page", defaultValue = "0") int page) {
        page = Math.max(0, page);
        Page<Team> return_page = teamService.getAll(page,PAGE_SIZE);
        for (Team t :return_page.getContent()) {
            t.getCreator().setPassword(null);
            t.getCreator().setEmail(null);
            t.getCreator().setIntro(null);
            t.clearLazyRoles();
        }
        return return_page;
    }
}

@Controller
@RequestMapping("/team")
class TeamViewController {
    @GetMapping
    public String showGroups() {
        return "team/teamindex";
    }
}
