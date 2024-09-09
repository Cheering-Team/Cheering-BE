package com.cheering.chat.ChatRoom;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 특정 선수 채팅방 목록 불러오기 (일단 대표만)
    @GetMapping("/players/{playerId}/chatrooms")
    public ResponseEntity<?> getChatRooms(@PathVariable("playerId") Long playerId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 목록을 불러왔습니다.", chatRoomService.getChatRooms(playerId)));
    }

    // 채팅방 정보 불러오기
    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> getChatRoomById(@PathVariable("chatRoomId") Long chatRoomId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 정보를 불러왔습니다.", chatRoomService.getChatRoomById(chatRoomId)));
    }
}
