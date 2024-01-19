package com.cheering.community.domain;

import com.cheering.community.constant.Category;
import com.cheering.community.constant.League;
import com.cheering.user.domain.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("team_community")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamCommunity extends Community {

    @OneToMany(mappedBy = "teamCommunity")
    private List<Player> players;
    private Category category;
    private League league;
}
