package com.cheering.apply;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.CommunityType;
import com.cheering.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apply_tb")
@Getter
@NoArgsConstructor
public class Apply extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column
    private String comment;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ApplyStatus status;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Builder
    public Apply (String content, User writer, ApplyStatus status) {
        this.content = content;
        this.writer = writer;
        this.status = status;
    }
}
