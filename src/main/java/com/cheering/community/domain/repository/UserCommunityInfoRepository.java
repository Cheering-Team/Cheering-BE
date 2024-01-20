package com.cheering.community.domain.repository;

import com.cheering.community.domain.Community;
import com.cheering.community.domain.UserCommunityInfo;
import com.cheering.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    boolean existsByUserAndCommunity(User user, Community community);

}
