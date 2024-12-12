package com.cheering;

import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.util.ApiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.serverAddress}")
    private String serverAddress;
    @Value("${serverName}")
    private String serverName;
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.app.version.latest}")
    private String latestVersion;
    @Value("${spring.app.version.minSupported}")
    private String minSupportedVersion;
    @Value("${spring.app.version.iosUrl}")
    private String iosUrl;
    @Value("${spring.app.version.aosUrl}")
    private String aosUrl;

    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("serverName", serverName);
        responseData.put("serverAddress", serverAddress);
        responseData.put("serverPort", serverPort);
        responseData.put("env", env);
        responseData.put("redisHost", redisHost);

        return ResponseEntity.ok(responseData);
    }
    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }

    @GetMapping("/api/version")
    public ResponseEntity<?> getVersionInfo() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("latestVersion", latestVersion);
        responseData.put("minSupportedVersion", minSupportedVersion);
        responseData.put("iosUrl", iosUrl);
        responseData.put("aosUrl", aosUrl);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, "버전 정보 조회", responseData));
    }
}
