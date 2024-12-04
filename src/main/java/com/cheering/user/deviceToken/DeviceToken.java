package com.cheering.user.deviceToken;

import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="device_token_tb")
public class DeviceToken {
    @Id
    @GeneratedValue
    @Column(name = "device_token_id")
    private Long id;

    @Column
    private String token;

    @Column
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public DeviceToken(String deviceId, String token, User user) {
        this.deviceId = deviceId;
        this.token = token;
        this.user = user;
    }
}
