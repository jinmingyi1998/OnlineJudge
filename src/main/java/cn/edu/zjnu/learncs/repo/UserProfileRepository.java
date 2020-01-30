package cn.edu.zjnu.learncs.repo;

import cn.edu.zjnu.learncs.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
