package com.cheering.post.Tag;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="tag_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder
    public Tag(Long tagId, String name) {
        this.id = tagId;
        this.name = name;
    }
}
