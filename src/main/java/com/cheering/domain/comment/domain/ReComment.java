package com.cheering.domain.comment.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.domain.community.domain.UserCommunityInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class ReComment {
    @Id
    @GeneratedValue
    @Column(name = "re_comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_community_info_id")
    private UserCommunityInfo writerInfo;
}
