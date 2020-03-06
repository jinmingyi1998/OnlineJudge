package cn.edu.zjnu.acm.repo.contest;

import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestCommentRepository extends JpaRepository<ContestComment, Long> {
    Optional<ContestComment> findById(Long id);

    List<ContestComment> findAllByContestOrderByIdDesc(Contest contest);
}