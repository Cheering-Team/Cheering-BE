package com.cheering.user.domain.repository;

import com.cheering.community.domain.UserCommunityInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {
}
