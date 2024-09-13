package com.cheering.user;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user_tb")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String phone;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column
    private String kakaoId;

    @Column
    private String naverId;


    @Builder
    public User(Long userId, String phone, String nickname, Role role, String kakaoId, String naverId) {
        this.id = userId;
        this.phone = phone;
        this.nickname = nickname;
        this.role = role;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
    }
}
