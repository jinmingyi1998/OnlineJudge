package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.oj.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
    Problem findProblemById(Long id);
    Page<Problem> findAll(Pageable pageable);
    Page<Problem> findProblemsByActive(Pageable pageable,Boolean active);
    List<Problem> findProblemsByActiveAndId(Boolean active,Long id);
    List<Problem> findProblemsByActiveAndTitleLike(Boolean active, String title);
    @Query("select p from Problem p where p.title like %:title% and p.active = :active")
    List<Problem> getProblemsByActiveAndTitle(@Param("active")Boolean active,@Param("title") String name);
}
