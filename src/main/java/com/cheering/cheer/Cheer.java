package com.cheering.cheer;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.match.Match;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cheer_tb")
@Getter
@Setter
@NoArgsConstructor
public class Cheer extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "cheer_id")
    private Long id;

    @Column(nullable = false)
    private CommunityType type;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Builder
    public Cheer(CommunityType type, String content, Long communityId, Fan writer, Match match) {
        this.type = type;
        this.content = content;
        this.communityId = communityId;
        this.writer = writer;
        this.match = match;
    }
}
