package com.cheering.user.TempAppleUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempAppleUserRepository extends JpaRepository<TempAppleUser, Long> {
    TempAppleUser findByAppleId(String appleId);

    void deleteByAppleId(String appleId);
}
