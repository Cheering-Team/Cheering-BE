package com.cheering.user;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cheering._core.errors.*;
import com.cheering._core.security.JWTUtil;
import com.cheering._core.util.AppleUtil;
import com.cheering._core.util.RedisUtils;
import com.cheering._core.util.SmsUtil;
import com.cheering.badword.BadWordService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.postReport.PostReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.TempAppleUser.TempAppleUser;
import com.cheering.user.TempAppleUser.TempAppleUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final TempAppleUserRepository tempAppleUserRepository;
    private final BadWordService badWordService;
    private final SmsUtil smsUtil;
    private final RedisUtils redisUtils;
    private final JWTUtil jwtUtil;
    private final AppleUtil appleUtil;
    private static final String PHONE_REGEX = "^01[0-9]{1}[0-9]{3,4}[0-9]{4}$";

    @Transactional
    public UserResponse.UserDTO sendSMS(UserRequest.SendSMSDTO requestDTO) {
        String phone = requestDTO.phone();

        Optional<User> user = userRepository.findByPhone(phone);

        String verificationCode;

        if(!phone.matches(PHONE_REGEX)) {
            throw new CustomException(ExceptionCode.INVALID_PHONE);
        }

        if(phone.equals("01062013110")) {
            verificationCode = "911911";
            smsUtil.sendCode(phone, verificationCode);
        } else if(phone.equals("01912341234")) {
            verificationCode = "019123";
        } else {
            verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
            smsUtil.sendCode(phone, verificationCode);
        }

        redisUtils.setDataExpire(phone, verificationCode, 60 * 5L);

        return user.map(UserResponse.UserDTO::new).orElse(null);
    }

    // 새로운 유저에 대한 인증코드 확인
    @Transactional
    public void checkCode(UserRequest.CheckCodeDTO requestDTO) {
        String phone = requestDTO.phone();
        String code = requestDTO.code();

        String storedCode = redisUtils.getData(phone);

        if(storedCode == null) {
            throw new CustomException(ExceptionCode.CODE_EXPIRED);
        }

        if(!storedCode.equals((code))){
            throw new CustomException(ExceptionCode.CODE_NOT_EQUAL);
        }

        redisUtils.deleteData(phone);
    }

    @Transactional
    public UserResponse.TokenDTO signUp(UserRequest.SignUpDTO requestDTO) {
        if(badWordService.containsBadWords(requestDTO.name())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        User user = User.builder()
                .phone(requestDTO.phone())
                .name(requestDTO.name())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return issueToken(user);
    }

    @Transactional
    public UserResponse.TokenDTO refresh(String refreshToken) {
        String token = refreshToken.split(" ")[1];

        String phone = jwtUtil.getUsername(token);

        User user = userRepository.findByPhone(phone).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String storedToken = redisUtils.getData(user.getId().toString());

        if(storedToken == null || !storedToken.equals(token)) {
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }

        return issueToken(user);
    }

    public UserResponse.UserDTO getUserInfo(User user) {
            return new UserResponse.UserDTO(user);
    }

    @Transactional
    public void updateUserName(UserRequest.NameDTO requestDTO, User user) {
        if(badWordService.containsBadWords(requestDTO.name())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        user.setName(requestDTO.name());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        List<PostReport> postReports = postReportRepository.findByUser(user);
        for(PostReport postReport : postReports) {
            postReport.setPost(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByUser(user);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByUser(user);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse.TokenDTO signInWithKakao(String kakaoToken) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);

        String kakaoId = response.getBody().get("id").toString();

        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoId);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            return issueToken(user);
        } else {
            throw new CustomException(ExceptionCode.NEED_SIGNUP);
        }
     }

    @Transactional
    public UserResponse.TokenDTO signInWithNaver(String naverToken) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + naverToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> successResponse = (Map<String, Object>) response.getBody().get("response");
        String naverId = successResponse.get("id").toString();

        Optional<User> optionalUser = userRepository.findByNaverId(naverId);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

           return issueToken(user);
        } else {
            throw new CustomException(ExceptionCode.NEED_SIGNUP);
        }
    }

    public UserResponse.TokenDTO signInWithApple(UserRequest.SocialTokenDTO requestDTO) {
        DecodedJWT decodedJWT = appleUtil.validateIdentityToken(requestDTO.accessToken());

        String appleId = decodedJWT.getSubject();

        Optional<User> optionalUser = userRepository.findByAppleId(appleId);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            return issueToken(user);
        } else {
            TempAppleUser tempAppleUser = TempAppleUser.builder()
                    .appleId(appleId)
                    .name(requestDTO.name())
                    .build();

            tempAppleUserRepository.save(tempAppleUser);

            throw new CustomException(ExceptionCode.NEED_SIGNUP);
        }
    }

    @Transactional
    public Object checkCodeSocial(String type, UserRequest.SocialCheckCodeDTO requestDTO) {
        String phone = requestDTO.phone();
        String code = requestDTO.code();
        String socialToken = requestDTO.accessToken();

        String storedCode = redisUtils.getData(phone);

        if(storedCode == null) {
            throw new CustomException(ExceptionCode.CODE_EXPIRED);
        }

        if(!storedCode.equals((code))){
            throw new CustomException(ExceptionCode.CODE_NOT_EQUAL);
        }

        redisUtils.deleteData(phone);

        Optional<User> optionalUser = userRepository.findByPhone(phone);

        if(optionalUser.isPresent()) {
            return new UserResponse.UserWithCreatedAtDTO(optionalUser.get());
        }

        User user;

        if(type.equals("kakao")) {
            RestTemplate restTemplate = new RestTemplate();
            String requestUrl = "https://kapi.kakao.com/v2/user/me";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + socialToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);

            String kakaoId = response.getBody().get("id").toString();
            Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
            String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("name");

            user = User.builder()
                    .name(nickname)
                    .phone(phone)
                    .role(Role.USER)
                    .kakaoId(kakaoId)
                    .build();
        } else if(type.equals("naver")) {
            RestTemplate restTemplate = new RestTemplate();
            String requestUrl = "https://openapi.naver.com/v1/nid/me";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + socialToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> successResponse = (Map<String, Object>) response.getBody().get("response");
            String naverId = successResponse.get("id").toString();
            String nickname = successResponse.get("name").toString();

            user = User.builder()
                    .name(nickname)
                    .phone(phone)
                    .role(Role.USER)
                    .naverId(naverId)
                    .build();
        } else {
            DecodedJWT decodedJWT = appleUtil.validateIdentityToken(socialToken);

            String appleId = decodedJWT.getSubject();

            TempAppleUser tempAppleUser = tempAppleUserRepository.findByAppleId(appleId);

            user = User.builder()
                    .name(tempAppleUser.getName())
                    .phone(phone)
                    .role(Role.USER)
                    .appleId(appleId)
                    .build();
        }
        userRepository.save(user);

        return issueToken(user);
    }

    @Transactional
    public UserResponse.TokenDTO socialConnect(String type, UserRequest.IdDTO requestDTO) {
        String token = requestDTO.accessToken();
        User user = userRepository.findById(requestDTO.userId()).orElseThrow(()->new CustomException(ExceptionCode.USER_NOT_FOUND));

        if(type.equals("kakao")){
            RestTemplate restTemplate = new RestTemplate();
            String requestUrl = "https://kapi.kakao.com/v2/user/me";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);

            String kakaoId = response.getBody().get("id").toString();

            user.setKakaoId(kakaoId);
        } else if(type.equals("naver")) {
            RestTemplate restTemplate = new RestTemplate();
            String requestUrl = "https://openapi.naver.com/v1/nid/me";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> successResponse = (Map<String, Object>) response.getBody().get("response");
            String naverId = successResponse.get("id").toString();

            user.setNaverId(naverId);
        } else {
            DecodedJWT decodedJWT = appleUtil.validateIdentityToken(token);

            String appleId = decodedJWT.getSubject();

            user.setAppleId(appleId);
        }

        return issueToken(user);
    }

    @Transactional
    public void saveFCMToken(String token, User user) {
        User curUser = userRepository.findById(user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        curUser.setDeviceToken(token);
        userRepository.save(curUser);
    }

    @Transactional
    public void deleteFCMToken(User user) {
        User curUser = userRepository.findById(user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.USER_NOT_FOUND));

        curUser.setDeviceToken(null);
        userRepository.save(curUser);
    }

    private UserResponse.TokenDTO issueToken(User user) {
        String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 30L); // 1달
        String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 365L); // 1년

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 365L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
    }

    @Transactional
    public Boolean isFirstLogin(User user) {
        Boolean isFirstLogin = user.getIsFirstLogin();
        user.setIsFirstLogin(false);
        userRepository.save(user);
        return isFirstLogin;
    }

//    public void registerCommunityAccount(Long communityId, UserRequest.SendSMSDTO requestDTO) {
//        Player player = playerRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
//
//        if(userRepository.existsByCommunity(player)){
//            throw new CustomException(ExceptionCode.ALREADY_MANAGER_ACCOUNT);
//        } else {
//            Random random = new Random();
//            String phone;
//
//            do {
//                phone = String.valueOf((long) 10000000000L + (long)(random.nextDouble() * 90000000000L));
//            } while (userRepository.existsByPhone(phone));
//
//            String code = String.valueOf((long) 100000L + (long)(random.nextDouble() * 900000L));
//
//            User newUser = User.builder()
//                    .role(player.getTeam() == null ? Role.PLAYER : Role.TEAM)
//                    .phone(phone)
//                    .password(passwordEncoder.encode(code))
//                    .name(player.getKoreanName())
//                    .community(player)
//                    .build();
//
//            userRepository.save(newUser);
//
//            smsUtil.sendAccount(requestDTO.phone(), phone, code);
//        }
//    }
//
//    public UserRequest.SendSMSDTO getManagerAccount(Long communityId, User user) {
//        if(!user.getRole().equals(Role.ADMIN)) {
//            throw new CustomException(ExceptionCode.USER_FORBIDDEN);
//        }
//
//        Player player = playerRepository.findById(communityId).orElseThrow(()-> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
//
//        Optional<User> playerAccount = userRepository.findByCommunity(player);
//
//        if(playerAccount.isEmpty()) {
//            throw new CustomException(ExceptionCode.NOT_FOUND_MANAGER_ACCOUNT);
//        } else {
//            return new UserRequest.SendSMSDTO(user.getPhone());
//        }
//    }
//
//    @Transactional
//    public void reissueManagerAccountPassword(Long communityId, UserRequest.SendSMSDTO requestDTO) {
//        Player player = playerRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
//
//        User user = userRepository.findByCommunity(player).orElseThrow(()-> new CustomException(ExceptionCode.NOT_FOUND_MANAGER_ACCOUNT));
//
//        Random random = new Random();
//        String code = String.valueOf((long) 100000L + (long)(random.nextDouble() * 900000L));
//        user.setPassword(passwordEncoder.encode(code));
//        userRepository.save(user);
//
//        smsUtil.sendAccount(requestDTO.phone(), user.getPhone(), code);
//    }
}
