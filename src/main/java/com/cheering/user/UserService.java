package com.cheering.user;

import com.cheering._core.errors.*;
import com.cheering._core.security.JWTUtil;
import com.cheering._core.util.RedisUtils;
import com.cheering._core.util.SmsUtil;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.community.UserCommunityInfoRepository;

import java.util.List;
import java.util.Optional;

import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostRepository;
import com.cheering.post.relation.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PlayerUserRepository playerUserRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final LikeRepository likeRepository;
    private final PostTagRepository postTagRepository;
    private final PostImageRepository postImageRepository;
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
        // 1. PlayerUser
        List<PlayerUser> playerUsers = playerUserRepository.findByUserId(user.getId());
        playerUserRepository.deleteByUserId(user.getId());

        // 2. Post
        List<Post> posts = postRepository.findByPlayerUserIn(playerUsers);

        // 3. PostTag
        postTagRepository.deleteByPostIn(posts);
        postImageRepository.deleteByPostIn(posts);

        postRepository.deleteByPlayerUserIn(playerUsers);

        // 3. Comment
        commentRepository.deleteByPlayerUserIn(playerUsers);

        // 4. ReComment
        reCommentRepository.deleteByPlayerUserIn(playerUsers);

        // 5. Like
        likeRepository.deleteByPlayerUserIn(playerUsers);

        // 5. User
        userRepository.delete(user);
    }
}
