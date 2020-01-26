/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.User;
import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.entity.oj.Solution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    Optional<Solution> findById(Long id);

    Page<Solution> findAll(Pageable pageable);

    Page<Solution> findAllByOrderByIdDesc(Pageable pageable);

    Page<Solution> findAllByUser(Pageable pageable, User user);

    List<Solution> findTop5ByUserAndProblemOrderByIdDesc(User user, Problem problem);

    List<Solution> findAllByUser(User user, Sort sort);

    Page<Solution> findAllByProblem(Pageable pageable, Problem problem);

    Page<Solution> findAllByResult(Pageable pageable, String result);

    Page<Solution> findAllByProblemAndResult(Pageable pageable, Problem problem, String result);

    //Top 50
    List<Solution> findFirst50ByResultAndProblemOrderByTimeAsc(String res, Problem problem);

    Page<Solution> findAllByUserAndResult(Pageable pageable, User user, String result);

    Page<Solution> findAllByUserAndProblemAndResult(Pageable pageable, User user, Problem problem, String result);

    Page<Solution> findAllByUserAndProblem(Pageable pageable, User user, Problem problem);

    @Transactional
    @Modifying
    @Query(value = "update solution set solution.result=:re , solution.time=:ti, solution.memory=:me where solution.id=:id", nativeQuery = true)
    void updateResultTimeMemory(@Param(value = "id") Long id,
                                @Param(value = "re") String result,
                                @Param(value = "ti") int time,
                                @Param(value = "me") int memory);

    @Transactional
    @Modifying
    @Query(value = "update solution set share = :share where id = :id", nativeQuery = true)
    void updateShare(@Param("id") Long id, @Param("share") Boolean share);

    @Transactional
    @Modifying
    @Query(value = "update solution set solution.result = :result , solution.info = :info where solution.id = :id", nativeQuery = true)
    void updateResultInfo(@Param("id") Long id, @Param("result") String result, @Param("info") String info);
    //   Page<Solution> findAllByUserAndProblemAndResult( Pageable pageable, User user, Problem problem, String result);

    Page<Solution> findAllByContestAndUser(Pageable pageable, Contest contest, User user);
}

