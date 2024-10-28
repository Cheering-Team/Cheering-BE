package com.cheering.report.block;

import com.cheering.fan.Fan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "block_tb")
@Getter
@Setter
@RequiredArgsConstructor
public class Block {
    @Id
    @GeneratedValue
    @Column(name = "block_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    private Fan from;

    @ManyToOne
    @JoinColumn(name = "to_id", nullable = false)
    private Fan to;

    @Builder
    public Block(Fan from, Fan to) {
        this.from = from;
        this.to = to;
    }
}
