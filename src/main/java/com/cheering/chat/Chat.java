package com.cheering.chat;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.message.Message;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chat extends BaseTimeEntity{
    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser writer;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Chat(ChatRoom chatRoom, PlayerUser writer) {
        this.chatRoom = chatRoom;
        this.writer = writer;
    }
}
