/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import cn.edu.zjnu.learncs.repo.SolutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class SolutionService {
    public static final String[] STATUS = {"Accepted", "Wrong Answer", "Compile Error",
            "Time Limit Exceed", "Memory Limit Exceed",
            "Runtime Error", "System Error"};

    @Autowired
    SolutionRepository solutionRepository;
    @Autowired
    private ProblemService problemService;

    /**
     * @param id solution id
     * @return a solution or null(not found)
     */
    public Solution getSolutionById(Long id) {
        return solutionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Solution insertSolution(Solution solution) {
        return solutionRepository.save(solution);
    }

    public Page<Solution> getStatus(int page, int size) {
        return solutionRepository.findAllByOrderByIdDesc(PageRequest.of(page, size));
    }

    public Page<Solution> getStatus(User user, Problem problem, String result, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        if (user != null) {
            if (problem != null) {
                if (result.length() > 0) {
                    return solutionRepository.findAllByUserAndProblemAndResult(pageable, user, problem, result);
                } else {
                    return solutionRepository.findAllByUserAndProblem(pageable, user, problem);
                }
            } else {
                if (result.length() > 0) {
                    return solutionRepository.findAllByUserAndResult(pageable, user, result);
                } else {
                    return solutionRepository.findAllByUser(pageable, user);
                }
            }
        } else {
            if (problem != null) {
                if (result != null && result.length() > 0) {
                    return solutionRepository.findAllByProblemAndResult(pageable, problem, result);
                } else {
                    return solutionRepository.findAllByProblem(pageable, problem);
                }
            } else {
                if (result != null && result.length() > 0) {
                    return solutionRepository.findAllByResult(pageable, result);
                } else {
                    return new PageImpl<Solution>(new LinkedList<>());
                }
            }
        }
    }

    public List<Solution> getTop50Problem(Problem problem) {
        return solutionRepository.findFirst50ByResultAndProblemOrderByTimeAsc("Accepted", problem);
    }


    /**
     * get a page by user id
     *
     * @param page
     * @param size
     * @param user
     * @return a page
     */
    /*public Page<Solution> getSolutionPage(int page, int size, User user) {
        Sort _sort = new Sort(Sort.Direction.DESC, "submitTime");
        Page<Solution> solutionPage = solutionRepository.findAllByUser(new PageRequest(page, size, _sort), user);
        return solutionPage;
    }

    public List<Solution> getSolutionsOfUser(User user) {
        return solutionRepository.findAllByUser(user, new Sort(Sort.Direction.DESC, "id"));
    }*/

    /**
     * get a page with sort
     *
     * @param page
     * @param size
     * @return
     */
    /*public Page<Solution> getSolutionPage(int page, int size) {
        Sort _sort = new Sort(Sort.Direction.DESC, "id");
        Page<Solution> solutionPage = solutionRepository.findAll(new PageRequest(page, size, _sort));
        return solutionPage;
    }*/
    @Transactional
    public Solution updateSolution(Solution solution) {
        return solutionRepository.save(solution);
    }
/*

    public List<Solution> getSolutionsOfUserInContest(User u, Contest c) {
        List<Solution> solutions = solutionRepository.findAllByUserAndContest(u, c);
        return solutions;
    }

    public List<Solution> getSolutionsInContest(Contest contest) {
        return solutionRepository.findAllByContestOrderByIdAsc(contest);
    }

    public void addSubmit(Solution solution) {
        try {
            @NotNull User user = solution.getUser();
            @NotNull Problem problem = solution.getProblem();
            user.setSubmit(user.getSubmit() + 1);
            problem.setSubmit(problem.getSubmit() + 1);
            userService.saveOrUpdateUser(user);
            problemService.updateProblem(problem);
            @NotNull Contest contest = solution.getContest();
            contest = contestService.getContestById(contest.getId());
            for (ContestProblem cp : contest.getProblems()) {
                if (cp.getProblem().getId() == problem.getId()) {
                    cp.setSubmitted(cp.getSubmitted() + 1);
                    contestProblemRepository.save(cp);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public void addAccepted(Solution solution) {
        try {
            @NotNull User user = solution.getUser();
            @NotNull Problem problem = solution.getProblem();
            problem.setAccepted(problem.getAccepted() + 1);
            problemService.updateProblem(problem);
            UserProblem userProblem = new UserProblem(user, problem);
            Optional<UserProblem> userProblem1 = userProblemRepository.findByUserAndProblem(user, problem);
            if (!userProblem1.isPresent()) {
                userProblemRepository.save(userProblem);
                user.addSocre(problem.getScore());
                user.setSolve(user.getSolve() + 1);
                userService.saveOrUpdateUser(user);
            }
            try {
                @NotNull Contest contest = solution.getContest();
                contest = contestService.getContestById(contest.getId());
                for (ContestProblem cp : contest.getProblems()) {
                    if (cp.getProblem().getId() == problem.getId()) {
                        cp.setAccepted(cp.getAccepted() + 1);
                        contestProblemRepository.save(cp);
                        break;
                    }
                }
            } catch (NullPointerException e) {
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("accept add failed");
        }
    }

    public UserProblem getUserProblem(User user,Problem problem)throws NullPointerException{
        Optional<UserProblem> userProblem=userProblemRepository.findByUserAndProblem(user,problem);
        if(userProblem.isPresent())return userProblem.get();
        throw new NullPointerException("didn't ac");
    }
    public List<UserProblem>getUserSolvedProblem(User user){
        List<UserProblem>userProblems=userProblemRepository.findAllByUser(user);
        return userProblems;
    }*/

}
