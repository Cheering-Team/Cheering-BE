package com.cheering.domain.post.repository;

import com.cheering.domain.post.domain.PostInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostInfoRepository extends JpaRepository<PostInfo, Long> {
}
