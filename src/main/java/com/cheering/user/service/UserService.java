package com.cheering.user.service;

import com.cheering.community.constant.BooleanType;
import com.cheering.community.domain.Community;
import com.cheering.community.domain.UserCommunityInfo;
import com.cheering.community.domain.repository.UserCommunityInfoRepository;
import com.cheering.community.dto.response.CommunityResponse;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.InvalidEmailFormatException;
import com.cheering.global.exception.user.MisMatchPasswordException;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.user.domain.Role;
import com.cheering.user.domain.User;
import com.cheering.user.domain.repository.UserRepository;
import com.cheering.user.dto.request.SignInRequest;
import com.cheering.user.dto.request.SignUpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public User signUp(SignUpRequest signUpRequest) {
        User newMember = User.builder()
                .email(signUpRequest.email())
                .password(signUpRequest.password())
                .nickname(signUpRequest.nickName())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public User signIn(SignInRequest signInRequest) {

        String email = signInRequest.email();
        String password = signInRequest.password();

        Optional<User> findUser = userRepository.findByEmailAndPassword(email, password);

        return findUser.orElseThrow(() ->
                new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getUserCommunities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginId = Long.valueOf(authentication.getName());

        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));

        List<UserCommunityInfo> userCommunities = userCommunityInfoRepository.findByUser(user);

        if (userCommunities.isEmpty()) {
            throw new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY);
        }

        List<CommunityResponse> result = new ArrayList<>();
        for (UserCommunityInfo userCommunityInfo : userCommunities) {
            Community community = userCommunityInfo.getCommunity();

            CommunityResponse communityResponse = new CommunityResponse(community.getId(),
                    community.getName(),
                    community.getImage(),
                    community.getFanCount(),
                    BooleanType.TRUE);

            result.add(communityResponse);
        }

        return result;
    }

    public void validateEmailFormat(String email) {
        //이메일 형식 검사 -> throw
        if (!Pattern.matches(REGEXP_EMAIL, email)) {
            throw new InvalidEmailFormatException(ExceptionMessage.INVALID_EMAIL_FORMAT);
        }
    }

    public void validateDuplicatedEmail(String email) {
        // 기존 이메일과 중복 검사
        if (userRepository.existsUserByEmail(email)) {
            throw new DuplicatedEmailException(ExceptionMessage.DUPLICATED_EMAIL);
        }
    }

    public void validateConfirmPassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MisMatchPasswordException(ExceptionMessage.INVALID_EMAIL_FORMAT);
        }
    }
}
