package com.cheering.notice;

import com.cheering.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "notice_tb")
@Getter
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String content;

    @Column
    private String image;
}
