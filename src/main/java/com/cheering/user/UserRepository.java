package com.cheering.user;

import com.cheering.community.Community;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByKakaoId(String kakaoId);
    Optional<User> findByNaverId(String naverId);
    Optional<User> findByAppleId(String appleId);

    boolean existsByPhone(String phone);

    boolean existsByCommunity(Community community);

    Optional<User> findByCommunity(Community community);
}
