/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package cn.edu.zjnu.acm.repo.contest;

import cn.edu.zjnu.acm.entity.oj.Contest;
import cn.edu.zjnu.acm.entity.oj.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Override
    Page<Contest> findAll(Pageable pageable);

    @Override
    List<Contest> findAll();

    List<Contest> findAllByTeam(Team team);

    Optional<Contest> findById(Long aLong);

    Page<Contest> findByTitleContains(Pageable pageable, String title);
}
