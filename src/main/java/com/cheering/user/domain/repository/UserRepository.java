package com.cheering.user.domain.repository;

import com.cheering.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<Member> findByEmailAndPassword(String email, String password);

    boolean existsUserByEmail(String email);


}
