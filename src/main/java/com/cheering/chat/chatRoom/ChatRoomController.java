package com.cheering.chat.chatRoom;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> getChatRooms(@PathVariable("playerId") Long playerId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 목록을 불러왔습니다.", chatRoomService.getChatRooms(playerId, customUserDetails.getUser())));
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

    // 채팅 불러오기
    @GetMapping("/chatrooms/{chatRoomId}/chats")
    public ResponseEntity<?> getChats(@PathVariable("chatRoomId") Long chatRoomId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅을 불러왔습니다.", chatRoomService.getChats(chatRoomId, pageable)));
    }

    // 참여자 불러오기
    @GetMapping("/chatrooms/{chatRoomId}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "참여자 목록을 불러왔습니다.", chatRoomService.getParticipants(chatRoomId, customUserDetails.getUser())));
    }

    // 채팅방 삭제하기
    @DeleteMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        chatRoomService.deleteChatRoom(chatRoomId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방을 삭제하였습니다.", null));
    }

    // (공식 채팅방 만들기)
    @PostMapping("/players/chatrooms")
    public ResponseEntity<?> autoCreateChatRooms() {
        chatRoomService.autoCreateChatRooms();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방들이 생성되었습니다.", null));
    }
}
