package com.cheering.community.domain;

import com.cheering.community.constant.Category;
import com.cheering.community.constant.League;
import com.cheering.global.BaseEntity;
import com.cheering.user.domain.Player;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
public class TeamCommunity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_community_id")
    private Long id;

    private String name;

    private Long fanCount;

    private String image;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Enumerated(value = EnumType.STRING)
    private League league;

    @OneToMany(mappedBy = "teamCommunity")
    private List<Player> players;
}
