package cn.edu.zjnu.acm.repo.problem;

import cn.edu.zjnu.acm.entity.oj.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Override
    List<Tag> findAll();

    Optional<Tag> findByName(String name);
}
