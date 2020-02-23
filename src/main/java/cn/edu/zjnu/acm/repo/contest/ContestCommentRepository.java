package cn.edu.zjnu.acm.repo.contest;

import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestCommentRepository extends JpaRepository<ContestComment, Long> {
    List<ContestComment> findAllByContestOrderByIdDesc(Contest contest);
}