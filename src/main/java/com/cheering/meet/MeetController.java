package com.cheering.meet;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/communities/{communityId}/meets")
    public ResponseEntity<?> createMeet(
            @PathVariable("communityId") Long communityId,
            @RequestBody MeetRequest.CreateMeetDTO requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        // 응답 반환
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임생성완료", meetService.createMeet(communityId, requestDto, user)));
    }


    // 모임 목록 조회
    @GetMapping("/meets")
    public ResponseEntity<?> getAllMeets(MeetRequest.MeetSearchRequest request) {
        MeetResponse.MeetListDTO meetList = meetService.findAllMeets(request);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 목록 조회 완료", meetList));
    }

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
                @PathVariable("communityId") Long communityId,
                @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        List<MeetResponse.MeetInfoDTO> meetList = meetService.findAllMeetsByCommunity(communityId, user);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "커뮤니티 내 모든 모임 조회 완료", meetList));
    }

    @DeleteMapping("/meets/{meetId}")
    public ResponseEntity<?> deleteMeet(@PathVariable Long meetId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        meetService.deleteMeet(meetId, user);
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "모임 삭제 완료", null));
    }




}
