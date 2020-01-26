package cn.edu.zjnu.learncs.repo;


import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.entity.oj.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Long> {
    ContestProblem save(ContestProblem contestProblem);

    List<ContestProblem> findAllByContest(Contest contest);

    Optional<ContestProblem> findByContestAndTempId(Contest contest, Long id);

}
