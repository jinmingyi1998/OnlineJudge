package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.oj.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByNameContaining(String name);

    List<Team> findAll();

    Page<Team> findAll(Pageable pageable);
}
