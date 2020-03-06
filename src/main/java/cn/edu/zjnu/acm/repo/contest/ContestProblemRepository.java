package cn.edu.zjnu.acm.repo.contest;


import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestProblem;
import cn.edu.zjnu.acm.entity.oj.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Long> {
    ContestProblem save(ContestProblem contestProblem);

    List<ContestProblem> findAllByContest(Contest contest);

    Optional<ContestProblem> findByContestAndTempId(Contest contest, Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE contest_problem set accepted=accepted+:ac where id = :pid", nativeQuery = true)
    void updateAcceptedNumber(@Param(value = "pid") Long pid, @Param(value = "ac") int ac);

    @Transactional
    @Modifying
    @Query(value = "UPDATE contest_problem set submitted=submitted+:sub where id = :pid", nativeQuery = true)
    void updateSubmittedNumber(@Param(value = "pid") Long pid, @Param(value = "sub") int sub);

    void deleteAllByProblem(Problem problem);
}
