package cn.edu.zjnu.acm.repo.user;

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
    void updateUserScore(@Param(value = "pid") Long profileId, @Param(value = "score") int score);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set score=:score where id = :pid", nativeQuery = true)
    void setUserScore(@Param(value = "pid") Long prolfileId, @Param(value = "score") int score);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set accepted=accepted+:ac where id = :pid", nativeQuery = true)
    void updateUserAccepted(@Param(value = "pid") Long profileId, @Param(value = "ac") int accepted);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set accepted=:ac where id = :pid", nativeQuery = true)
    void setUserAccepted(@Param(value = "pid") Long profileId, @Param(value = "ac") int accepted);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set submitted=submitted+:sub where id = :pid", nativeQuery = true)
    void updateUserSubmitted(@Param(value = "pid") Long profileId, @Param(value = "sub") int sub);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_profile set submitted=:sub where id = :pid", nativeQuery = true)
    void setUserSubmitted(@Param(value = "pid") Long profileId, @Param(value = "sub") int sub);



}
