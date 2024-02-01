package com.cheering.domain.community.domain.repository;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    List<UserCommunityInfo> findByUser(User user);

    Optional<UserCommunityInfo> findByUserAndCommunity(User user, Community community);

    boolean existsByUserAndCommunity(User user, Community community);

}
