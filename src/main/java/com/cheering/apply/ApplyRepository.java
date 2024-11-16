package com.cheering.apply;

import com.cheering.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
    @Query("SELECT a FROM Apply a WHERE a.writer = :writer ORDER BY CASE WHEN a.status = 'PENDING' THEN 0 ELSE 1 END, a.createdAt DESC")
    List<Apply> findByWriter(@Param("writer") User user);
}
