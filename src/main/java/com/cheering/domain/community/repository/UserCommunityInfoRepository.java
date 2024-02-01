package com.cheering.domain.community.repository;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    List<UserCommunityInfo> findByUser(User user);

    List<UserCommunityInfo> findByCommunity(Community community);

    boolean existsByUserAndCommunity(User user, Community community);

}
