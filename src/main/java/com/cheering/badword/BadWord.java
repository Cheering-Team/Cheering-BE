package com.cheering.badword;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "badword_tb")
@Getter
@NoArgsConstructor
public class BadWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badword_id")
    private Long id;

    @Column
    private String word;

    @Builder
    public BadWord (String word) {
        this.word = word;
    }
}
