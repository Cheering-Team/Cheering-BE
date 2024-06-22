package com.cheering.post;

import com.cheering.community.Community;
import com.cheering.user.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
//    List<Post> findByWriterInfoCommunityAndWriterInfoUser(Community community, User user);

//    List<Post> findByWriterInfoCommunityAndTeam(Community community, Team team);

//    List<Post> findByWriterInfoCommunityAndWriterInfoUserIsNotNull(Community community);
}
