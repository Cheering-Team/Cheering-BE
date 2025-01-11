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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class  ChatRoomController {
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
    // 대표 채팅방 조회
    @GetMapping("/communities/{communityId}/chatrooms/official")
    public ResponseEntity<?> getOfficialChatRoom(@PathVariable("communityId") Long communityId) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "대표 채팅방 조회 완료", chatRoomService.getOfficialChatRoom(communityId)));
    }

    // 일반 채팅방 목록 조회
    @GetMapping("/communities/{communityId}/chatrooms")
    public ResponseEntity<?> getChatRooms(@PathVariable("communityId") Long communityId, @RequestParam String sortBy, @RequestParam String name, @RequestParam int page, @RequestParam int size, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방 목록 조회 완료", chatRoomService.getChatRooms(communityId, sortBy, name, pageable, customUserDetails.getUser())));
    }

    // 참여중인 대표 채팅방 목록 조회
    @GetMapping("/my/chatrooms/official")
    public ResponseEntity<?> getMyOfficialChatRooms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "대표 채팅방 목록 조회 완료", chatRoomService.getMyOfficialChatRooms(customUserDetails.getUser())));
    }

    // 참여중인 일반 채팅방 목록 조회
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
    public ResponseEntity<?> getChats(@PathVariable("chatRoomId") Long chatRoomId,
                                      @RequestParam(required = false) LocalDateTime cursorDate,
                                      @RequestParam(defaultValue = "20") int size,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅 목록 조회", chatRoomService.getChats(chatRoomId, cursorDate, size, customUserDetails.getUser())));
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

    // 채팅방 퇴장 시간 갱신
    @PutMapping("/chat-rooms/{chatRoomId}/exit-time")
    public ResponseEntity<?> updateExitTime(@PathVariable Long chatRoomId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        chatRoomService.updateExitTime(chatRoomId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "퇴장 시간 갱신", null));
    }

    // 안읽은 전체 채팅 수
    @GetMapping("/chats/unread")
    public ResponseEntity<?> getUnreadChats(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "안읽은 채팅 수", chatRoomService.getUnreadChats(customUserDetails.getUser())));
    }

    // (공식 채팅방 만들기)
    @PostMapping("/communities/chatrooms")
    public ResponseEntity<?> autoCreateChatRooms() {
        chatRoomService.autoCreateChatRooms();
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "채팅방들이 생성되었습니다.", null));
    }

    // 1:1 채팅방 만들기(모임)
    @PostMapping("/communities/{communityId}/meets/{meetId}/talk")
    public ResponseEntity<?> createPrivateChatRoom(
            @PathVariable Long communityId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ChatRoomResponse.IdWithConditionDTO chatRoomIdWithCondition = chatRoomService.createPrivateChatRoom(communityId, meetId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "1대1 채팅방 생성 완료", chatRoomIdWithCondition));
    }

    // 특정 모임에 대해 온 1:1 채팅방 목록 조회
    @GetMapping("/meets/{meetId}/private")
    public ResponseEntity<?> getPrivateChatRoomsForManager(
            @PathVariable Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "1대1 채팅방 목록 조회 완료", chatRoomService.getPrivateChatRoomsForManager(meetId, userDetails.getUser())));
    }


}
