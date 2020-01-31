package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.oj.Team;
import cn.edu.zjnu.acm.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    public Page<Team> getAll(int page, int size) {
        return teamRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

}
