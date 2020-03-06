package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.ContestComment;
import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.repo.CommentRepository;
import cn.edu.zjnu.acm.repo.contest.ContestCommentRepository;
import cn.edu.zjnu.acm.repo.contest.ContestProblemRepository;
import cn.edu.zjnu.acm.repo.contest.ContestRepository;
import lombok.extern.slf4j.Slf4j;
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
    private final ContestRepository contestRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final SolutionService solutionService;
    private final ContestCommentRepository contestCommentRepository;

    public ContestService(ContestRepository contestRepository, ContestProblemRepository contestProblemRepository, SolutionService solutionService, CommentRepository commentRepository, ContestCommentRepository contestCommentRepository) {
        this.contestRepository = contestRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.solutionService = solutionService;
        this.contestCommentRepository = contestCommentRepository;
    }

    public Page<Contest> getContestPage(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (title.length() == 0)
            return contestRepository.findAll(pageable);
        return contestRepository.findByTitleContains(pageable, title);
    }

    public Page<Contest> getContestWithoutTeam(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        if (title.length() == 0)
            return contestRepository.findAllByPrivilegeContains(pageable, "p");
        return contestRepository.findByTitleContainsAndPrivilegeContains(pageable, title, "p");
    }

    public List<Contest> getContestList() {
        return contestRepository.findAll();
    }

    public Contest saveContest(Contest contest) {
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
            contest.setContestComments(getCommentsOfContest(contest));
            return contest;
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContestComment> getCommentsOfContest(Contest c) {
        List<ContestComment> comments = contestCommentRepository.findAllByContestOrderByIdDesc(c);
        return comments;
    }

    public void postComment(ContestComment contestComment) {
        contestCommentRepository.save(contestComment);
    }

    public ContestComment getFatherComment(Long id) {
        return contestCommentRepository.findById(id).orElse(null);
    }

    public List<Contest> contestsOfTeam(Team team) {
        return contestRepository.findAllByTeam(team);
    }

    @Transactional
    public void deleteContest(Contest contest) {
        contestRepository.delete(contest);
    }
}
