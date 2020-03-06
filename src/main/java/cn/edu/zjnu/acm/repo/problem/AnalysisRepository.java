package cn.edu.zjnu.acm.repo.problem;

import cn.edu.zjnu.acm.entity.oj.Analysis;
import cn.edu.zjnu.acm.entity.oj.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findAllByProblem(Problem problem);

    Optional<Analysis> findById(Long id);

    void deleteAllByProblem(Problem problem);
}
