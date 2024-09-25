package com.cheering.user;

import com.cheering._core.errors.*;
import com.cheering._core.security.JWTUtil;
import com.cheering._core.util.RedisUtils;
import com.cheering._core.util.SmsUtil;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostRepository;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.postReport.PostReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final SmsUtil smsUtil;
    private final RedisUtils redisUtils;
    private final JWTUtil jwtUtil;

    @Transactional
    public UserResponse.UserDTO sendSMS(UserRequest.SendSMSDTO requestDTO) {
        String phone = requestDTO.phone();

        Optional<User> user = userRepository.findByPhone(phone);

        boolean isUser = user.isPresent();

        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        smsUtil.sendOne(phone, verificationCode);

        redisUtils.setDataExpire(phone, verificationCode, 60 * 5L);

        if(isUser) {
            return new UserResponse.UserDTO(user.get());
        } else {
            return null;
        }
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
        User user = User.builder()
                .phone(requestDTO.phone())
                .nickname(requestDTO.nickname())
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().toString(), 1000 * 60 * 60 * 24L);
        String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().toString(), 1000 * 60 * 60 * 24 * 365L);

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 365L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
    }

    @Transactional
    public UserResponse.TokenDTO refresh(String refreshToken) {
        System.out.println("재발급 시도");

        String token = refreshToken.split(" ")[1];

        String phone = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = userRepository.findByPhone(phone).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String storedToken = redisUtils.getData(user.getId().toString());

        if(!storedToken.equals(token)) {
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }

        String accessToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24L);
        refreshToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24 * 30L);

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
    }

    public UserResponse.UserDTO getUserInfo(User user) {
        return new UserResponse.UserDTO(user);
    }

    @Transactional
    public void updateUserNickname(UserRequest.NicknameDTO requestDTO, User user) {
        user.setNickname(requestDTO.nickname());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        List<PostReport> postReports = postReportRepository.findByUserId(user.getId());
        for(PostReport postReport : postReports) {
            postReport.setPost(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByUserId(user.getId());
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByUserId(user.getId());
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
            String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24L);
            String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 30L);

            redisUtils.deleteData(user.getId().toString());
            redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

            return new UserResponse.TokenDTO(accessToken, refreshToken);
        } else {
            return null;
        }
     }

    @Transactional
    public Object signInWithNaver(String naverToken) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + naverToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> successResponse = (Map<String, Object>) response.getBody().get("response");
        String naverId = successResponse.get("id").toString();
        String mobile = successResponse.get("mobile").toString().replace("-", "");
        String nickname = successResponse.get("nickname").toString();

        Optional<User> optionalUser = userRepository.findByPhone(mobile);

        // 이미 가입된 번호일 때 연동
        if(optionalUser.isPresent()) {
            if(optionalUser.get().getNaverId() != null && optionalUser.get().getNaverId().equals(naverId)) {
                String accessToken = jwtUtil.createJwt(optionalUser.get().getPhone(), optionalUser.get().getRole().getValue(), 1000 * 60 * 60 * 24L);
                String refreshToken = jwtUtil.createJwt(optionalUser.get().getPhone(), optionalUser.get().getRole().getValue(), 1000 * 60 * 60 * 24 * 30L);


                redisUtils.deleteData(optionalUser.get().getId().toString());
                redisUtils.setDataExpire(optionalUser.get().getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

                return new UserResponse.TokenDTO(accessToken, refreshToken);
            }
            return new UserResponse.UserWithCreatedAtDTO(optionalUser.get());
        }

        User user = User.builder()
                .nickname(nickname)
                .phone(mobile)
                .role(Role.ROLE_USER)
                .naverId(naverId)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24L);
        String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 30L);


        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

        // 가입되지 않은 번호일 때, 회원가입 후 자동 로그인
        return new UserResponse.SignUpTokenDTO(accessToken, refreshToken);
    }

    @Transactional
    public Object checkCodeKakao(String kakaoToken,UserRequest.CheckCodeDTO requestDTO) {
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

        Optional<User> optionalUser = userRepository.findByPhone(phone);

        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Map.class);

        String kakaoId = response.getBody().get("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");

        if(optionalUser.isPresent()) {
           return new UserResponse.UserWithCreatedAtDTO(optionalUser.get());
        }

        User user = User.builder()
                    .nickname(nickname)
                    .phone(phone)
                    .role(Role.ROLE_USER)
                    .kakaoId(kakaoId)
                    .build();

        userRepository.save(user);

        String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24L);
        String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 30L);

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
    }

    @Transactional
    public UserResponse.TokenDTO socialConnect(String token, String type, UserRequest.IdDTO requestDTO) {
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
        }

        String accessToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24L);
        String refreshToken = jwtUtil.createJwt(user.getPhone(), user.getRole().getValue(), 1000 * 60 * 60 * 24 * 30L);

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 30L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
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
}
