package com.cheering.meetfan;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.Fan;
import com.cheering.meet.Meet;
import com.cheering.meetfan.MeetFanRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meet_fan_tb")
@Getter
@Setter
@NoArgsConstructor
public class MeetFan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meet_fan_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetFanRole role; // 모임에서의 역할 (매니저/구성원)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id", nullable = false)
    private Meet meet; // 연결된 모임

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_id", nullable = false)
    private Fan fan; // 연결된 팬

    @Builder
    public MeetFan(MeetFanRole role, Meet meet, Fan fan) {
        this.role = role;
        this.meet = meet;
        this.fan = fan;
    }
    public MeetFan(Long meetFanId) {
        this.id = meetFanId;
    }
}
