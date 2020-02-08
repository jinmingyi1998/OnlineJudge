package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.exception.NeedLoginException;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.service.ProblemService;
import cn.edu.zjnu.acm.service.SolutionService;
import cn.edu.zjnu.acm.service.UserService;
import cn.edu.zjnu.acm.util.UserGraph;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/user")
public class UserSpaceController {
    private final UserService userService;
    private final SolutionService solutionService;
    private final ProblemService problemService;
    @Autowired
    HttpSession session;

    public UserSpaceController(UserService userService, SolutionService solutionService, ProblemService problemService) {
        this.userService = userService;
        this.solutionService = solutionService;
        this.problemService = problemService;
    }

    @GetMapping("/{uid:[0-9]+}")
    public User getUserInfo(@PathVariable(value = "uid") Long uid) {
        User user = userService.getUserById(uid);
        if (user == null) throw new NotFoundException();
        user.setPassword(null);
        return user;
    }

    @GetMapping("/pie/{uid:[0-9]+}")
    public UserGraph getUserGraph(@PathVariable(value = "uid") Long uid) {
        User user = userService.getUserById(uid);
        if (user == null)
            throw new NotFoundException();
        UserGraph userGraph = new UserGraph();
        userGraph.getPie().setPrime(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("初级"), false));
        userGraph.getPie().setMedium(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("中级"), false));
        userGraph.getPie().setAdvance(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("高级"), false));
        userGraph.getRadar().setData_structure(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("数据结构"), true));
        userGraph.getRadar().setDynamic_programming(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("动态规划"), true));
        userGraph.getRadar().setSearch(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("搜索"), true));
        userGraph.getRadar().setGraph_theory(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("图论"), true));
        userGraph.getRadar().setProbability(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("概率论"), true));
        userGraph.getRadar().setMath(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("数论"), true));
        userGraph.getRadar().setString(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("字符串"), true));
        userGraph.getRadar().setGeometry(solutionService.countSolveProblemByTag(user,
                problemService.getTagByName("计算几何"), true));
        userGraph.getRadar().init();
        return userGraph;
    }

    @PostMapping("/edit/{uid:[0-9]+}")
    public String registerUser(@RequestBody UpdateUser updateUser,
                               @PathVariable(value = "uid") Long uid) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) throw new NeedLoginException();
        if (currentUser.getId() != uid) throw new NotFoundException();
        User user = userService.getUserById(uid);
        if (!userService.checkPassword(updateUser.getOldpassword(), user.getPassword()))
            return "Old Password Wrong";
        user.setPassword(updateUser.getPassword());
        user.setIntro(updateUser.getIntro());
        user.setEmail(updateUser.getEmail());
        user.setName(updateUser.getName());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userService.updateUserInfo(user);
        return "success";
    }

    @Data
    static class UpdateUser {
        @Size(min = 1, max = 30)
        String name;
        @Size(min = 1, max = 60)
        String password;
        @Size(min = 1, max = 60)
        String oldpassword;
        @Size(max = 250)
        String intro;
        @Email
        @Size(min = 4, max = 200)
        String email;
    }
}

@Controller
@RequestMapping("/user")
class UserSpaceViewController {
    @GetMapping("/{uid:[0-9]+}")
    public String showUser() {
        return "user/userspace";
    }

    @GetMapping("/edit/{uid:[0-9]+}")
    public String editUser() {
        return "user/update_user";
    }
}