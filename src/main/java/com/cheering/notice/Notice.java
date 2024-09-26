package com.cheering.notice;

import com.cheering.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notice_tb")
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @Column
    private String title;
}
