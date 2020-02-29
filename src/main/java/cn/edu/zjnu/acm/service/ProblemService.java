package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.*;
import cn.edu.zjnu.acm.repo.problem.AnalysisCommentRepository;
import cn.edu.zjnu.acm.repo.problem.AnalysisRepository;
import cn.edu.zjnu.acm.repo.problem.ProblemRepository;
import cn.edu.zjnu.acm.repo.problem.TagRepository;
import cn.edu.zjnu.acm.repo.user.UserProblemRepository;
import cn.edu.zjnu.acm.util.PageHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final TagRepository tagRepository;
    private final UserProblemRepository userProblemRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisCommentRepository analysisCommentRepository;

    public ProblemService(ProblemRepository problemRepository, TagRepository tagRepository, UserProblemRepository userProblemRepository, AnalysisCommentRepository analysisCommentRepository, AnalysisRepository analysisRepository) {
        this.problemRepository = problemRepository;
        this.tagRepository = tagRepository;
        this.userProblemRepository = userProblemRepository;
        this.analysisCommentRepository = analysisCommentRepository;
        this.analysisRepository = analysisRepository;
    }

    public Page<Problem> getAllActiveProblems(int page, int size) {
        return problemRepository.findProblemsByActive(PageRequest.of(page, size), true);
    }

    public Page<Problem> getAllProblems(int page, int size, String search) {
        return problemRepository.findAllByTitleContaining(PageRequest.of(page, size), search);
    }

    public List<Problem> getProblemList() {
        return problemRepository.findAll();
    }

    public Page<Problem> getByTagName(int page, int size, List<String> tagNames, List<Problem> problems) {
        problems = new ArrayList<>(problems);
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName).orElse(null);
            if (tag == null) {
                continue;
            }
            List<Problem> _tags = problemRepository.findAllByTags(tag);
            problems.retainAll(_tags);
        }
        page = Math.min((problems.size() - 1) / size, page);
        return new PageHolder<Problem>(problems, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
    }

    public Page<Problem> searchActiveProblem(int page, int size, String search, boolean allInOnePage) {
        List<Problem> problems = new LinkedList<>();
        try {
            Long pid = Long.parseLong(search);
            problems.addAll(problemRepository.findProblemsByActiveAndId(true, pid));
        } catch (Exception e) {
            log.debug("parse int failed");
        }
        try {
            problems.addAll(problemRepository.findAllByActiveAndTitleContaining(true, search));
        } catch (Exception e) {
            log.debug("search problem by title failed");
        }
        HashSet<Problem> set = new HashSet<>(problems);
        List<Problem> _problems = new ArrayList<>(set);
        if (allInOnePage) {
            size = Math.max(_problems.size(), 1);
            page = 0;
        }
        return new PageHolder<>(_problems, PageRequest.of(page, size));
    }

    public Problem getActiveProblemById(Long id) {
        return problemRepository.findProblemByIdAndActive(id, true).orElse(null);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Problem insertNewProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public Boolean isProblemRepeated(String title) {
        return problemRepository.findByTitle(title).isPresent();
    }

    public Problem getProblemById(Long id) {
        return problemRepository.findById(id).orElse(null);
    }

    public List<Tag> convertString2Tag(String s) {
        String[] ts = s.split("[,，]");
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < ts.length; i++) {
            Tag t = tagRepository.findByName(ts[i]).orElse(null);
            if (t != null) {
                tags.add(t);
            }
        }
        return tags;
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name).orElse(null);
    }

    public Boolean isUserAcProblem(User user, Problem problem) {
        return userProblemRepository.existsAllByUserAndProblem(user, problem);
    }

    public List<Problem> allUserAcProblems(User user) {
        return userProblemRepository.findAllByUser(user).stream()
                .map(UserProblem::getProblem).collect(Collectors.toList());
    }

    public List<Analysis> getAnalysisByProblem(Problem problem) {
        return analysisRepository.findAllByProblem(problem);
    }

    public Analysis getAnalysisById(Long id) {
        return analysisRepository.findById(id).orElse(null);
    }

    public Analysis postAnalysis(Analysis analysis) {
        return analysisRepository.save(analysis);
    }
    public AnalysisComment postAnalysisComment(AnalysisComment comment){
        return analysisCommentRepository.save(comment);
    }
    public AnalysisComment getFatherComment(Long id){
        return analysisCommentRepository.findById(id).orElse(null);
    }
}
