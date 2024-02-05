package com.cheering.domain.community.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.community.constant.Category;
import com.cheering.domain.community.constant.CommunityType;
import com.cheering.domain.community.constant.League;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.net.URL;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Community extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "community_id")
    private Long id;

    private String name;

    private Long fanCount;

    private URL image;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Enumerated(value = EnumType.STRING)
    private League league;

    @OneToOne(fetch = LAZY, mappedBy = "community")
    private User user;

    @Enumerated(EnumType.STRING)
    private CommunityType cType;

    @OneToOne(fetch = LAZY, mappedBy = "teamCommunity")
    private Team team;

}
