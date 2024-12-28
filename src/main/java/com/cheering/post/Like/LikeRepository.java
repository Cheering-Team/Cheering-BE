package com.cheering.post.Like;

import com.cheering.fan.Fan;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByTargetIdAndTargetType(Long targetId, String targetType);
    Optional<Like> findByTargetIdAndTargetTypeAndFan(Long targetId, String targetType, Fan fan);

    void deleteByTargetIdAndTargetType(Long targetId, String targetType);
}
