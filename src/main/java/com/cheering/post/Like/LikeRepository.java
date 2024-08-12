package com.cheering.post.Like;

import com.cheering.player.relation.PlayerUser;
import com.cheering.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPostId(Long postId);
    @Query("select like from Like like where like.post.id=:postId and like.playerUser.id=:playerUserId")
    Optional<Like> findByPostIdAndPlayerUserId (@Param("postId") Long postId, @Param("playerUserId") Long playerUserId );

    void deleteByPlayerUserIn(List<PlayerUser> playerUsers);
    void deleteByPlayerUser(PlayerUser playerUser);
    void deleteByPost(Post post);
}
