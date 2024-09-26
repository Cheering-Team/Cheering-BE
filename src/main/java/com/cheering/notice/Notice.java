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

    @Column
    private String title;
}
