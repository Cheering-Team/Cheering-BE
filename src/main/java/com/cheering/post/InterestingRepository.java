package com.cheering.post;

import com.cheering.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestingRepository extends JpaRepository<Interesting, Long> {

    Optional<Interesting> findByUserAndPost(User user, Post post);
}
