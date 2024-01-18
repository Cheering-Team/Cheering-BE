package com.cheering.community.controller;

import com.cheering.community.dto.CommunityResponse;
import com.cheering.community.service.CommunityService;
import com.cheering.global.constant.SuccessMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class communityController {

    private final CommunityService communityService;

    @GetMapping("/communities")
    public ResponseEntity<ResponseBodyDto<?>> findCommunitiesByName(@RequestParam("name") String name) {
        List<CommunityResponse> communitiesByName = communityService.findCommunitiesByName(name);
        
        return ResponseGenerator.success(SuccessMessage.SEARCH_COMMUNITY_SUCCESS, communitiesByName);
    }

    @GetMapping("/set-data")
    @Transactional
    public String setData() {
        communityService.setData();
        return null;
    }
}
