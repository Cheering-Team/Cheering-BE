package com.cheering.notice.apply;

import com.cheering.BaseTimeEntity;
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
    Long id;

    @Column
    String field1;

    @Column
    String field2;

    @Column
    String field3;

    @Column
    String field4;

    @Column
    String image;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Builder
    public Apply(String field1, String field2, String field3, String field4, String image) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.image = image;
    }
}
