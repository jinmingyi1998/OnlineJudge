package cn.edu.zjnu.acm.repo.user;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Problem;
import cn.edu.zjnu.acm.entity.oj.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {
    Long countAllByUser(User user);

    Long countAllByProblem(Problem problem);

    List<UserProblem> findAllByUser(User user);

    Boolean existsAllByUserAndProblem(User user, Problem problem);

    @Query(nativeQuery = true,
            value = "SELECT COALESCE( SUM(p.score),0) " +
                    "FROM user_problem AS up, problem AS p " +
                    "WHERE p.id=up.problem_id AND up.user_id=:uid")
    Long calculateUserScore(@Param(value = "uid") Long uid);

    void deleteAllByProblem(Problem problem);
}
