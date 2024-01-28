package com.cheering.community.domain.repository;

import com.cheering.community.domain.Community;
import com.cheering.community.domain.UserCommunityInfo;
import com.cheering.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    List<UserCommunityInfo> findByUser(User user);

    boolean existsByUserAndCommunity(User user, Community community);

}
