package com.cheering.community.domain;

import com.cheering.user.domain.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
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
    List<Player> players = new ArrayList<>();

}
