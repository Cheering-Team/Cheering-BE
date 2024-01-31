package com.cheering.core.community.domain.repository;

import com.cheering.core.community.domain.Community;
import com.cheering.core.community.domain.UserCommunityInfo;
import com.cheering.core.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    List<UserCommunityInfo> findByUser(User user);

    boolean existsByUserAndCommunity(User user, Community community);

}
