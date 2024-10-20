package com.cheering.team.sport;

import com.cheering._core.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SportController {
    private final SportService sportService;

    @GetMapping("/sports")
    public ResponseEntity<?> getSports() {
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "종목 조회 완료", sportService.getSports()));
    }
}
