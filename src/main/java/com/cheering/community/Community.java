package com.cheering.community;

import com.cheering.community.relation.Fan;
import com.cheering.post.Post;
import com.cheering.team.Team;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="community_tb")
@Getter
@Setter
public class Community {
    @Id
    @GeneratedValue
    @Column(name = "community_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommunityType type;

    @Column(nullable = false)
    private String koreanName;

    @Column
    private String englishName;

    @Column
    private String image;

    @Column
    private String backgroundImage;

    // 연결된 팀 (팀 커뮤니티일 경우)
    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // 커뮤니티 관리자 (해당 커뮤니티의 주인이 참여했을 경우)
    @OneToOne
    @JoinColumn(name = "manager_id")
    private Fan manager;

    @OneToOne(mappedBy = "community")
    private User user;

    @OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE)
    private List<Fan> fans = new ArrayList<>();

    @Builder
    public Community(CommunityType type, String koreanName, String englishName, String image, String backgroundImage, Team team) {
        this.type = type;
        this.koreanName = koreanName;
        this.englishName = englishName;
        this.image = image;
        this.backgroundImage = backgroundImage;
        this.team = team;
    }
}
