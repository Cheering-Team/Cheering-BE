package com.cheering.chat;

import com.cheering.fan.FanResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatGroup {
    private ChatType type;
    private LocalDateTime createdAt;
    private FanResponse.FanDTO writer;
    private List<String> messages;
    private String groupKey;
}
