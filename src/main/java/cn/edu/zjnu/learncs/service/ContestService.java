package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.entity.oj.Comment;
import cn.edu.zjnu.learncs.entity.oj.Contest;
import cn.edu.zjnu.learncs.repo.CommentRepository;
import cn.edu.zjnu.learncs.repo.ContestProblemRepository;
import cn.edu.zjnu.learncs.repo.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class ContestService {
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    ContestProblemRepository contestProblemRepository;
    @Autowired
    SolutionService solutionService;
    @Autowired
    CommentRepository commentRepository;

    public Page<Contest> getContestPage(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        if (title.length() == 0)
            return contestRepository.findAll(pageable);
        return contestRepository.findByTitleContains(pageable, title);
    }

    public Contest insertContest(Contest contest) {
        return contestRepository.save(contest);
    }

    @Transactional
    public Contest getContestById(Long id) {
        return getContestById(id, false);
    }

    @Transactional
    public Contest getContestById(Long id, boolean isAllFields) {
        if (isAllFields) {
            try {
                return fulfillContest(contestRepository.findById(id).get());
            } catch (Exception e) {
                return null;
            }
        }
        return contestRepository.findById(id).orElse(null);
    }

    private Contest fulfillContest(Contest contest) {
        try {
            contest.setProblems(contestProblemRepository.findAllByContest(contest));
            contest.setSolutions(solutionService.getSolutionsInContest(contest));
            return contest;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Comment> getCommentsOfContest(Contest c) {
        List<Comment> comments = commentRepository.findAllByContest(c);
        return comments;
    }

    public Comment postComment(Comment Comment) {
        log.info(Comment.toString());
        return commentRepository.save(Comment);
    }

}
