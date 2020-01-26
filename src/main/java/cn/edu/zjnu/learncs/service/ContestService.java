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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class ContestService {
    private final int PAGE_SIZE = 30;
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    ContestProblemRepository contestProblemRepository;
    @Autowired
    SolutionService solutionService;
    @Autowired
    CommentRepository commentRepository;

    public Page<Contest> getContestPage(int page, String title) {
        if (title.length() == 0)
            return contestRepository.findAll(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "startTime")));
        return contestRepository.findByTitleContains(PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "startTime")), "%" + title + "%");
    }

    public Contest insertContest(Contest contest) {
        return contestRepository.save(contest);
    }

    @Transactional
    public Contest getContestById(Long id) {
        Optional<Contest> contest = contestRepository.findById(id);
        if (contest.isPresent()) {// initialize the contest
            //contest.get().setSolutions(solutionService.getSolutionsInContest(contest.get()));
            contest.get().setProblems(contestProblemRepository.findAllByContest(contest.get()));
            return contest.get();
        } else return null;
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
