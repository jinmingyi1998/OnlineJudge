package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.oj.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Override
    List<Tag> findAll();

    Optional<Tag> findByName(String name);
}
