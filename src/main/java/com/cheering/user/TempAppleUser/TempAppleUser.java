package com.cheering.user.TempAppleUser;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="temp_apple_user_tb")
public class TempAppleUser {
    @Id
    @GeneratedValue
    @Column(name = "temp_apple_user_id")
    private Long id;

    @Column(nullable = false)
    private String appleId;

    @Column(nullable = false)
    private String name;

    @Builder
    public TempAppleUser (String appleId, String name) {
        this.appleId = appleId;
        this.name = name;
    }
}
