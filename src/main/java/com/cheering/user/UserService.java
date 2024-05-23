package com.cheering.user;

import com.cheering._core.errors.*;
import com.cheering._core.security.CustomUserDetails;
import com.cheering._core.security.JWTUtil;
import com.cheering._core.util.RedisUtils;
import com.cheering._core.util.SmsUtil;
import com.cheering.community.UserCommunityInfoRepository;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final static String REGEXP_EMAIL = "^[A-Za-z0-9_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]*\\.*[A-Za-z0-9\\-]+$";

    private final UserRepository userRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final SmsUtil smsUtil;
    private final RedisUtils redisUtils;
    private final JWTUtil jwtUtil;

    @Transactional
    public UserResponse.UserDTO sendSMS(UserRequest.SendSMSDTO requestDTO) {
        String phone = requestDTO.phone();

        Optional<User> user = userRepository.findByPhone(phone);

        boolean isUser = user.isPresent();

        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
//        smsUtil.sendOne(phone, verificationCode);

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
        String token = refreshToken.split(" ")[1];

        String phone = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        System.out.println("3");

        User user = userRepository.findByPhone(phone).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String storedToken = redisUtils.getData(user.getId().toString());

        if(!storedToken.equals(token)) {
            System.out.println("4");
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }
        System.out.println("5");

        String accessToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24L);
        refreshToken = jwtUtil.createJwt(phone, role, 1000 * 60 * 60 * 24 * 365L);

        redisUtils.deleteData(user.getId().toString());
        redisUtils.setDataExpire(user.getId().toString(), refreshToken, 1000 * 60 * 60 * 24 * 365L);

        return new UserResponse.TokenDTO(accessToken, refreshToken);
    }

//    @Transactional
//    public User signUp(SignUpRequest signUpRequest) {
//        User newMember = User.builder()
//                .email(signUpRequest.email())
//                .password(signUpRequest.password())
//                .nickname(signUpRequest.nickName())
//                .role(Role.ROLE_USER)
//                .build();
//
//        return userRepository.save(newMember);
//    }
//
//    @Transactional(readOnly = true)
//    public User signIn(UserRequest userRequest) {
//
//        String email = userRequest.email();
//        String password = userRequest.password();
//
//        Optional<User> findUser = userRepository.findByEmailAndPassword(email, password);
//
//        return findUser.orElseThrow(() ->
//                new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
//    }
//
//    @Transactional(readOnly = true)
//    public List<SearchCommunityResponse> getUserCommunities() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long loginId = Long.valueOf(authentication.getName());
//
//        User user = userRepository.findById(loginId)
//                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
//
//        List<UserCommunityInfo> userCommunities = userCommunityInfoRepository.findByUser(user);
//
//        if (userCommunities.isEmpty()) {
//            throw new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY);
//        }
//
//        List<SearchCommunityResponse> result = new ArrayList<>();
//        for (UserCommunityInfo userCommunityInfo : userCommunities) {
//            Community community = userCommunityInfo.getCommunity();
//
//            SearchCommunityResponse searchCommunityResponse = new SearchCommunityResponse(community.getId(),
//                    community.getName(),
//                    community.getThumbnailImage(),
//                    community.getFanCount(),
//                    BooleanType.TRUE);
//
//            result.add(searchCommunityResponse);
//        }
//
//        return result;
//    }
//
//    public void validateEmailFormat(String email) {
//        //이메일 형식 검사 -> throw
//        if (!Pattern.matches(REGEXP_EMAIL, email)) {
//            throw new InvalidEmailFormatException(ExceptionMessage.INVALID_EMAIL_FORMAT);
//        }
//    }
//
//    public void validateDuplicatedEmail(String email) {
//        // 기존 이메일과 중복 검사
//        if (userRepository.existsUserByEmail(email)) {
//            throw new DuplicatedEmailException(ExceptionMessage.DUPLICATED_EMAIL);
//        }
//    }
//
//    public void validateConfirmPassword(String password, String passwordConfirm) {
//        if (!password.equals(passwordConfirm)) {
//            throw new MisMatchPasswordException(ExceptionMessage.INVALID_EMAIL_FORMAT);
//        }
//    }
}
