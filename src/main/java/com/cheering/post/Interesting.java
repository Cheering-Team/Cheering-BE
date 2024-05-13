package com.cheering.post;

import static jakarta.persistence.FetchType.LAZY;

import com.cheering.community.BooleanType;
import com.cheering.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Interesting {
    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BooleanType status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public BooleanType changeStatus() {
        if (BooleanType.TRUE.equals(status)) {
            status = BooleanType.FALSE;
        } else {
            status = BooleanType.TRUE;
        }
        
        return status;
    }
}
