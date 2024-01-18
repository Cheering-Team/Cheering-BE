package com.cheering.community.domain.repository;

import com.cheering.community.domain.Community;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("select c from Community c where c.name = :name")
    List<Community> findCommunitiesByName(@Param("name") String name);
}
