package com.cheering.domain.post.repository;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.domain.Player;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunityAndUser(Community community, User user);

    List<Post> findByCommunityAndPlayer(Community community, Player player);

    List<Post> findByCommunityAndTeam(Community community, Team team);
}
