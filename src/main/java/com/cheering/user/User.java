package com.cheering.user;

import com.cheering.BaseTimeEntity;
import com.cheering.apply.Apply;
import com.cheering.fan.Fan;
import com.cheering.user.deviceToken.DeviceToken;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(length = 15, nullable = false, unique = true)
    private String phone;

    @Column(length = 20, nullable = false)
    private String name;

    @Column
    private String password;

    @Column
    private String kakaoId;

    @Column
    private String naverId;

    @Column
    private String appleId;

    @Column
    private Boolean isFirstLogin;

    @Column
    private Integer age;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Fan> fans = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Apply> applies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<DeviceToken> deviceTokens = new ArrayList<>();

    @Builder
    public User(String phone, String name, Role role, String kakaoId, String naverId, String appleId, String password, Integer age, Gender gender) {
        this.role = role;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.appleId = appleId;
        this.isFirstLogin = true;
        this.age = age;
        this.gender = gender;
    }
}
