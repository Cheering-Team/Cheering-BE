package com.cheering.meet;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.chat.chatRoom.ChatRoomService;
import com.cheering.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Meet API", description = "Operations related to Meet management")
public class MeetController {

    private final MeetService meetService;
    private final ChatRoomService chatRoomService;

    @PostMapping("/communities/{communityId}/meets")
    public ResponseEntity<?> createMeet(
            @PathVariable("communityId") Long communityId,
            @RequestBody MeetRequest.CreateMeetDTO requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임생성완료", meetService.createMeet(communityId, requestDto, user)));
    }

/*
    // 모임 목록 조회
    @GetMapping("/meets")
    public ResponseEntity<?> getAllMeets(MeetRequest.MeetSearchRequest request) {
        MeetResponse.MeetListDTO meetList = meetService.findAllMeets(request);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 목록 조회 완료", meetList));
    }
*/
    //모임 상세 조회
    @GetMapping("/meets/{meetId}")
    public ResponseEntity<?> getMeetDetail(
            @PathVariable("meetId") Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        MeetResponse.MeetDetailDTO response = meetService.getMeetDetail(meetId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 상세 조회 완료", response));
    }

    @GetMapping("/communities/{communityId}/meets")
    public ResponseEntity<?> getAllMeetsByCommunity(
            MeetRequest.MeetSearchRequest request,
                @PathVariable("communityId") Long communityId,
                @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        MeetResponse.MeetListDTO meetList = meetService.findAllMeetsByCommunity(request, communityId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 내 모든 모임 조회 완료", meetList));
    }

    @DeleteMapping("/meets/{meetId}")
    public ResponseEntity<?> deleteMeet(@PathVariable Long meetId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        meetService.deleteMeet(meetId, user);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 삭제 완료", null));
    }

    @PutMapping("/meets/{meetId}")
    public ResponseEntity<?> updateMeet(
            @PathVariable Long meetId,
            @RequestBody @Valid MeetRequest.UpdateMeetDTO updateMeetDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        meetService.updateMeet(meetId, updateMeetDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 수정 완료", null));
    }

    @PostMapping("/communities/{communityId}/meets/{meetId}/private-chat")
    public ResponseEntity<?> createPrivateChatRoom(
            @PathVariable Long communityId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ChatRoomResponse.IdDTO chatRoomId = chatRoomService.createPrivateChatRoom(communityId, meetId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "1대1 채팅방 생성 완료", chatRoomId));
    }

}
