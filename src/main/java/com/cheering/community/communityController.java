package com.cheering.community;

import com.cheering._core.errors.SuccessMessage;
import com.cheering._core.errors.ResponseBodyDto;
import com.cheering._core.errors.ResponseGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class communityController {

    private final CommunityService communityService;

    @GetMapping("/communities")
    public ResponseEntity<ResponseBodyDto<?>> findCommunitiesByName(@RequestParam("name") String name) {
        List<FoundCommunitiesResponse> communitiesByName = communityService.findCommunitiesByName(name);

        return ResponseGenerator.success(SuccessMessage.SEARCH_COMMUNITY_SUCCESS, communitiesByName);
    }

    @PostMapping("/communities/{id}/users")
    public ResponseEntity<ResponseBodyDto<?>> joinCommunity(@PathVariable("id") Long communityId,
                                                            @RequestPart(value = "image", required = false) MultipartFile file,
                                                            @RequestParam("nickname") String nickname) {
        UserCommunityInfoResponse response = communityService.joinCommunity(communityId, nickname, file);

        return ResponseGenerator.success(SuccessMessage.JOIN_COMMUNITY_SUCCESS, response);
    }

    @GetMapping("/communities/{id}")
    public ResponseEntity<ResponseBodyDto<?>> getCommunity(@PathVariable("id") Long communityId) {

        CommunityResponse communityResponse = communityService.getCommunity(communityId);

        return ResponseGenerator.success(SuccessMessage.GET_COMMUNITY_SUCCESS, communityResponse);
    }

    @GetMapping("/set-data")
    @Transactional
    public String setData() {
        communityService.setData();
        return null;
    }

}
