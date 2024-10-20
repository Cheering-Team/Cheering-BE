package com.cheering.report.block;

import com.cheering.community.relation.Fan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("SELECT b FROM Block b WHERE b.from = :from AND b.to = :to")
    Optional<Block> findByFromAndTo(@Param("from") Fan from, @Param("to") Fan to);

    @Query("SELECT b.to FROM Block b WHERE b.from = :from")
    List<Fan> findToByFrom(@Param("from") Fan from);

    @Modifying
    @Query("DELETE FROM Block b WHERE b.from = :from AND b.to = :to")
    void deleteByFromAndTo(@Param("from") Fan curFan, @Param("to") Fan fan);
}
