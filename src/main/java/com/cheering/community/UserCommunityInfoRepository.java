package com.cheering.community;

import com.cheering.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityInfoRepository extends JpaRepository<UserCommunityInfo, Long> {

    List<UserCommunityInfo> findByUser(User user);

    List<UserCommunityInfo> findByCommunity(Community community);

    Optional<UserCommunityInfo> findByUserAndCommunity(User user, Community community);

    boolean existsByUserAndCommunity(User user, Community community);

}
