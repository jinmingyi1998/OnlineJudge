package cn.edu.zjnu.acm.repo.problem;

import cn.edu.zjnu.acm.entity.oj.Analysis;
import cn.edu.zjnu.acm.entity.oj.AnalysisComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisCommentRepository extends JpaRepository<AnalysisComment,Long> {
    List<AnalysisComment> findAllByAnalysis(Analysis analysis);
    AnalysisComment save(AnalysisComment analysisComment);
}
