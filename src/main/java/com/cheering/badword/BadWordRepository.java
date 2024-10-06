package com.cheering.badword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadWordRepository extends JpaRepository<BadWord, Long> {
    Optional<BadWord> findByWord(String word);
}
