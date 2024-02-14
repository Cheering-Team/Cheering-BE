package com.cheering.domain.post.repository;

import com.cheering.domain.post.domain.Interesting;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestingRepository extends JpaRepository<Interesting, Long> {

    Optional<Interesting> findByUserAndPost(User user, Post post);
}
