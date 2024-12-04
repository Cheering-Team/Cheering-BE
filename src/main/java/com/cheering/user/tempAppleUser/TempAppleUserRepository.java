package com.cheering.user.tempAppleUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempAppleUserRepository extends JpaRepository<TempAppleUser, Long> {
    TempAppleUser findByAppleId(String appleId);
}
