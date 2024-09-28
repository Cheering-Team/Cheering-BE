package com.cheering.chat.ChatRoom;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 특정 선수 채팅방 만들기
    @PostMapping("/players/{playerId}/chatrooms")
    public ResponseEntity<?> createChatRoom(@PathVariable("playerId") Long playerId, @RequestParam(value = "name") String name,
                                            @RequestParam(value = "description", required = false) String description,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam(value = "max") Integer max,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방을 개설하였습니다.", chatRoomService.createChatRoom(playerId, name, description, image, max, customUserDetails.getUser())));
    }

    // 특정 선수 채팅방 목록 불러오기
    @GetMapping("/players/{playerId}/chatrooms")
    public ResponseEntity<?> getChatRooms(@PathVariable("playerId") Long playerId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 목록을 불러왔습니다.", chatRoomService.getChatRooms(playerId)));
    }

    // 내 참여중인 모든 채팅방 불러오기
    // (대표는 가입된 모두 커뮤니티)
    @GetMapping("/my/chatrooms")
    public ResponseEntity<?> getMyChatRooms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "내 채팅방 목록을 불러왔습니다.", chatRoomService.getMyChatRooms(customUserDetails.getUser())));
    }

    // 채팅방 정보 불러오기
    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> getChatRoomById(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 정보를 불러왔습니다.", chatRoomService.getChatRoomById(chatRoomId, customUserDetails.getUser())));
    }
}
