package com.cheering.user;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String phone;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Builder
    public User(Long userId, String phone, String nickname, Role role) {
        this.id = userId;
        this.phone = phone;
        this.nickname = nickname;
        this.role = role;
    }
}
