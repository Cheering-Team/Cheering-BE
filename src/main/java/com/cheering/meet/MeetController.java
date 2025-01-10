package com.cheering.meet;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Meet API", description = "Operations related to Meet management")
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/communities/{communityId}/meets")
    @Operation(summary = "Create Meet", description = "해당 커뮤니티에 모임을 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 생성 성공", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MeetResponse.MeetIdDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> createMeet(
            @PathVariable("communityId") Long communityId,
            @Valid @RequestBody MeetRequest.CreateMeetDTO requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임생성완료", meetService.createMeet(communityId, requestDto, user)));
    }

    //모임 상세 조회
    @GetMapping("/meets/{meetId}")
    @Operation(summary = "Get Meet Details", description = "특정 모임의 상세 정보를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 상세 조회 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MeetResponse.MeetDetailDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> getMeetDetail(
            @PathVariable("meetId") Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        MeetResponse.MeetDetailDTO response = meetService.getMeetDetail(meetId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 상세 조회 완료", response));
    }

    @GetMapping("/communities/{communityId}/meets")
    @Operation(summary = "Get All Meets by Community", description = "특정 커뮤니티의 모든 모임을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "커뮤니티 내 모든 모임 조회 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = MeetResponse.MeetListDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "커뮤니티를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> getAllMeetsByCommunity(
            MeetRequest.MeetSearchRequest request,
                @PathVariable("communityId") Long communityId,
                @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        MeetResponse.MeetListDTO meetList = meetService.findAllMeetsByCommunity(request, communityId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 내 모든 모임 조회 완료", meetList));
    }

    @DeleteMapping("/meets/{meetId}")
    @Operation(summary = "Delete Meet", description = "특정 모임을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> deleteMeet(@PathVariable Long meetId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        meetService.deleteMeet(meetId, user);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 삭제 완료", null));
    }

    @PutMapping("/meets/{meetId}")
    @Operation(summary = "Update Meet", description = "특정 모임의 정보를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> updateMeet(
            @PathVariable Long meetId,
            @RequestBody @Valid MeetRequest.UpdateMeetDTO updateMeetDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        meetService.updateMeet(meetId, updateMeetDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 수정 완료", null));
    }

    /* 챗룸컨트롤러에서 구현함
    @PostMapping("/communities/{communityId}/meets/{meetId}/private-chat")
    public ResponseEntity<?> createPrivateChatRoom(
            @PathVariable Long communityId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ChatRoomResponse.IdDTO chatRoomId = chatRoomService.createPrivateChatRoom(communityId, meetId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "1대1 채팅방 생성 완료", chatRoomId));
    }
    */

    @PostMapping("/chatrooms/{chatRoomId}/accept")
    public ResponseEntity<?> acceptJoinRequest(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        meetService.acceptJoinRequest(chatRoomId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "사용자가 모임에 가입되었습니다.", null));
    }

    @PostMapping("/meets/{meetId}/cancel")
    public ResponseEntity<?> cancelMeetParticipation(
            @PathVariable Long meetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        meetService.cancelMeetParticipation(meetId, user);

        return ResponseEntity.ok().body(
                ApiUtils.success(HttpStatus.OK, "모임 참여가 취소되었습니다.", null)
        );
    }

    @GetMapping("/check-existing-meet")
    public ResponseEntity<?> checkExistingMeet(@RequestParam Long matchId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        boolean exists = meetService.checkExistingMeet(matchId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "기존 모임 확인 완료", exists));
    }

    @GetMapping("/meets/my")
    public ResponseEntity<?> findMyMeets(MeetRequest.MeetSearchRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        MeetResponse.MeetListDTO myMeets = meetService.findAllMyMeets(request, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "내가 참여하는 모든 모임 조회 완료", myMeets));
    }

}
