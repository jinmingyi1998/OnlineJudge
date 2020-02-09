package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.oj.Comment;
import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.repo.CommentRepository;
import cn.edu.zjnu.acm.repo.ContestProblemRepository;
import cn.edu.zjnu.acm.repo.ContestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
            contest.setContestComments(commentRepository.findAllByContestOrderByPostTimeDesc(contest));
            return contest;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Comment> getCommentsOfContest(Contest c) {
        List<Comment> comments = commentRepository.findAllByContestOrderByPostTimeDesc(c);
        return comments;
    }

    public Comment postComment(Comment Comment) {
        return commentRepository.save(Comment);
    }

    public List<Contest> contestsOfTeam(Team team) {
        return contestRepository.findAllByTeam(team);
    }
}
