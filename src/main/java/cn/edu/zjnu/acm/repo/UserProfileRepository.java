package cn.edu.zjnu.acm.repo;

import cn.edu.zjnu.acm.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    @Override
    <S extends UserProfile> S save(S entity);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set score=score+:score where id = :pid", nativeQuery = true)
    public void updateUserScore(@Param(value = "pid") Long pid, @Param(value = "score") int score);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set accepted=accepted+:ac where id = :pid", nativeQuery = true)
    public void updateUserAccepted(@Param(value = "pid") Long pid, @Param(value = "ac") int accepted);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set submitted=submitted+:sub where id = :pid", nativeQuery = true)
    public void updateUserSubmitted(@Param(value = "pid") Long pid, @Param(value = "sub") int sub);
}
