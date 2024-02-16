package com.cheering.domain.post.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.user.domain.Team;
import com.cheering.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
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
public class Post extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_community_info_id")
    private UserCommunityInfo writerInfo;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "post")
    private List<ImageFile> files;

}
