package com.cheering.user;

import com.cheering.BaseTimeEntity;
import com.cheering.notice.apply.Apply;
import com.cheering.community.Community;
import com.cheering.community.relation.Fan;
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
    private String deviceToken;

    // 선수, 팀 계정일경우 자신과 연결된 커뮤니티
    @OneToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Fan> fans = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.REMOVE)
    private List<Apply> applies = new ArrayList<>();

    @Builder
    public User(Long userId, String phone, String name, Role role, String kakaoId, String naverId, String appleId, Community community, String password) {
        this.id = userId;
        this.role = role;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.appleId = appleId;
        this.community = community;
    }
}
