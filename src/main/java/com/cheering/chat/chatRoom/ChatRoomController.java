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

    // 채팅방 생성
    @PostMapping("/communities/{communityId}/chatrooms")
    public ResponseEntity<?> createChatRoom(@PathVariable("communityId") Long communityId, @RequestParam(value = "name") String name,
                                            @RequestParam(value = "description", required = false) String description,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam(value = "max") Integer max,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 생성 완료", chatRoomService.createChatRoom(communityId, name, description, image, max, customUserDetails.getUser())));
    }

    // 채팅방 목록 조회
    @GetMapping("/communities/{communityId}/chatrooms")
    public ResponseEntity<?> getChatRooms(@PathVariable("communityId") Long communityId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 목록 조회 완료", chatRoomService.getChatRooms(communityId, customUserDetails.getUser())));
    }

    // 참여중인 채팅방 목록 조회
    // (대표는 가입된 모두 커뮤니티)
    @GetMapping("/my/chatrooms")
    public ResponseEntity<?> getMyChatRooms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "참여 채팅방 목록 조회 완료", chatRoomService.getMyChatRooms(customUserDetails.getUser())));
    }

    // 채팅방 정보 조회
    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> getChatRoomById(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 정보 조회 완료", chatRoomService.getChatRoomById(chatRoomId, customUserDetails.getUser())));
    }

    // 채팅 목록 조회
    @GetMapping("/chatrooms/{chatRoomId}/chats")
    public ResponseEntity<?> getChats(@PathVariable("chatRoomId") Long chatRoomId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅 목록 조회", chatRoomService.getChats(chatRoomId, pageable)));
    }

    // 채팅 참여자 조회
    @GetMapping("/chatrooms/{chatRoomId}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅 참여자 조회 완료", chatRoomService.getParticipants(chatRoomId, customUserDetails.getUser())));
    }

    // 채팅방 삭제
    @DeleteMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable("chatRoomId") Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        chatRoomService.deleteChatRoom(chatRoomId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 삭제 완료", null));
    }

    // (공식 채팅방 만들기)
    @PostMapping("/communities/chatrooms")
    public ResponseEntity<?> autoCreateChatRooms() {
        chatRoomService.autoCreateChatRooms();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방들이 생성되었습니다.", null));
    }
}
