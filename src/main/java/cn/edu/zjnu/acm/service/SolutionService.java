package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.Solution;
import cn.edu.zjnu.acm.repo.problem.SolutionRepository;
import cn.edu.zjnu.acm.util.PageHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SolutionService {
    public static final String[] STATUS = {Solution.AC, Solution.WA, Solution.CE,
            Solution.TLE, Solution.MLE,
            Solution.RE, Solution.SE};

    private final SolutionRepository solutionRepository;

    public SolutionService(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }


    public Solution getSolutionById(Long id) {
        return solutionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Solution insertSolution(Solution solution) {
        try {
            return solutionRepository.save(solution);
        } catch (Exception e) {
            return null;
        }
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
                    return new PageHolder<Solution>();
                }
            }
        }
    }

    public List<Solution> getTop50Problem(Problem problem) {
        return solutionRepository.findFirst50ByResultAndProblemOrderByTimeAsc("Accepted", problem);
    }

    public List<Solution> getProblemSubmitOfUser(User user, Problem problem) {
        return solutionRepository.findAllByUserAndProblemOrderByIdDesc(user, problem);
    }

    public Solution updateSolutionResultTimeMemoryCase(Solution solution) {
        solutionRepository.updateResultTimeMemoryCase(solution.getId(),
                solution.getResult(),
                solution.getTime(),
                solution.getMemory(), solution.getCaseNumber());
        return solution;
    }

    public Solution updateSolutionShare(Solution solution) {
        solutionRepository.updateShare(solution.getId(), solution.getShare());
        return solution;
    }

    public Solution updateSolutionResultInfo(Solution solution) {
        solutionRepository.updateResultInfo(solution.getId(), solution.getResult(), solution.getInfo());
        return solution;
    }

    public Page<Solution> getSolutionsOfUserInContest(int page, int size, User u, Contest c) {
        return solutionRepository.findAllByContestAndUser(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")), c, u);
    }

    public List<Solution> getSolutionsInContest(Contest contest) {
        return solutionRepository.findAllByContestOrderByIdDesc(contest);
    }

    public Long countAcOfProblem(Problem problem) {
        return solutionRepository.countAllByProblemAndResult(problem, Solution.AC);
    }

    public Long countOfProblem(Problem problem) {
        return solutionRepository.countAllByProblem(problem);
    }

    public Long countAcOfProblemContest(Problem problem, Contest contest) {
        return solutionRepository.countAllByContestAndProblemAndResult(contest, problem, Solution.AC);
    }

    public Long countOfProblemContest(Problem problem, Contest contest) {
        return solutionRepository.countAllByContestAndProblem(contest, problem);
    }

}
