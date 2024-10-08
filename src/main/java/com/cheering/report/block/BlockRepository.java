package com.cheering.report.block;

import com.cheering.player.relation.PlayerUser;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("SELECT b FROM Block b WHERE b.from = :from AND b.to = :to")
    Optional<Block> findByFromAndTo(@Param("from") PlayerUser from, @Param("to") PlayerUser to);
}
