package com.cheering.team.sport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {

    @Query("SELECT s FROM Sport s ORDER BY CASE " +
            "WHEN s.name = '야구' THEN 1 " +
            "WHEN s.name = '축구' THEN 2 " +
            "WHEN s.name = '농구' THEN 3 " +
            "ELSE 4 END")
    List<Sport> findSportsInCustomOrder();
}
