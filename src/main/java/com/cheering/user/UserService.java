package com.cheering.user;

import com.cheering._core.errors.*;
import com.cheering._core.security.JWTUtil;
import com.cheering._core.util.RedisUtils;
import com.cheering._core.util.SmsUtil;
import com.cheering.community.UserCommunityInfoRepository;

import java.util.List;
import java.util.Optional;

import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PlayerUserRepository playerUserRepository;
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
        playerUserRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }
}
