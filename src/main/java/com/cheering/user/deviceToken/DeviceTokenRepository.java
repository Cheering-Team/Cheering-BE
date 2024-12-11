package com.cheering.user.deviceToken;

import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByDeviceIdAndUser(String deviceId, User curUser);

    void deleteByDeviceIdAndUser(String deviceId, User user);

    void deleteByToken(String token);
}
