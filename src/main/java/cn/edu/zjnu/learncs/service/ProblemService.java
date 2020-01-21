package cn.edu.zjnu.learncs.service;

import cn.edu.zjnu.learncs.entity.oj.Problem;
import cn.edu.zjnu.learncs.repo.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;

    public Page<Problem> getAllActiveProblems(int page, int size) {
        return problemRepository.findProblemsByActive(PageRequest.of(page, size), true);
    }

    public Page<Problem> searchActiveProblem(int page, int size, String search) {
        List<Problem> problems = new LinkedList<>();
        try {
            Long pid = Long.parseLong(search);
            problems.addAll(problemRepository.findProblemsByActiveAndId(true, pid));
        } catch (Exception e) {
            log.debug("parse int failed");
        }
        try {
            problems.addAll(problemRepository.getProblemsByActiveAndTitle(true, search));
        } catch (Exception e) {
            log.debug("search problem by title failed");
        }
        HashSet<Problem> set = new HashSet<>(problems);
        List<Problem> _problems = new ArrayList<>(set);
        return new PageImpl<>(_problems, PageRequest.of(page, size), _problems.size());
    }

    public Problem getActiveProblemById(Long id) {
        return problemRepository.findProblemByIdAndActive(id, true).orElse(null);
    }

}
