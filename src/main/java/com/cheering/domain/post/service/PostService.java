package com.cheering.domain.post.service;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.repository.CommunityRepository;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.Player;
import com.cheering.domain.user.domain.Role;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.domain.repository.PlayerRepository;
import com.cheering.domain.user.domain.repository.TeamRepository;
import com.cheering.domain.user.domain.repository.UserRepository;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundTeamException;
import com.cheering.global.exception.user.NotFoundUserException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> getPosts(Long communityId, Long writerId, String type) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        List<Post> result = getPostsByType(type, writerId, findCommunity);
        
        return PostResponse.ofList(result);
    }

    private List<Post> getPostsByType(String type, Long writerId, Community findCommunity) {
        List<Post> result = new ArrayList<>();

        if (Role.ROLE_PLAYER.getType().equals(type)) {
            Player findPlayer = playerRepository.findById(writerId)
                    .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
            result = postRepository.findByCommunityAndPlayer(findCommunity, findPlayer);
        }

        if (Role.ROLE_USER.getType().equals(type)) {
            User findUser = userRepository.findById(writerId)
                    .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
            result = postRepository.findByCommunityAndUser(findCommunity, findUser);
        }

        if (Role.ROLE_TEAM.getType().equals(type)) {
            Team findTeam = teamRepository.findById(writerId)
                    .orElseThrow(() -> new NotFoundTeamException(ExceptionMessage.NOT_FOUND_TEAM));
            result = postRepository.findByCommunityAndTeam(findCommunity, findTeam);
        }

        return result;
    }
}
