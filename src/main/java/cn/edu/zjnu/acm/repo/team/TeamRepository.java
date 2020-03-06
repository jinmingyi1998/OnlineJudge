package cn.edu.zjnu.acm.repo.team;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.oj.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByNameContaining(String name);

    List<Team> findAll();

    Optional<Team> findByName(String name);

    Page<Team> findAll(Pageable pageable);

    Long countAllByCreator(User user);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE `team` SET `attend` = :attend WHERE `id` = :tid")
    void updateAttendById(@Param("attend") String attend, @Param("tid") Long id);
}
