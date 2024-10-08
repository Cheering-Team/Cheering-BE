package com.cheering.report.block;

import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
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
    @JoinColumn(name = "from_id")
    private PlayerUser from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private PlayerUser to;

    @Builder
    public Block(PlayerUser from, PlayerUser to) {
        this.from = from;
        this.to = to;
    }
}
