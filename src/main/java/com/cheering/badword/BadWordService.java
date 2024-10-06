package com.cheering.badword;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadWordService {
    private final BadWordRepository badWordRepository;
    private Set<String> badWordsCache = new HashSet<>();

    @Scheduled(fixedRate = 3600000) // 1시간마다 캐시 갱신
    public void scheduledBadWordsCacheUpdate() {
        refreshBadWordsCache();
    }
    public boolean containsBadWords(String text) {
        for (String badWord : badWordsCache) {
            if (text.replaceAll("\\s+", "").contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    public void refreshBadWordsCache() {
        badWordsCache.clear();
        badWordsCache.addAll(badWordRepository.findAll().stream()
                .map(BadWord::getWord)
                .collect(Collectors.toSet()));
    }

    public void addBadWord(String word) {
        Optional<BadWord> badWord = badWordRepository.findByWord(word);

        if(badWord.isEmpty()) {
            BadWord newBadWord = BadWord.builder()
                    .word(word)
                    .build();

            badWordRepository.save(newBadWord);

            refreshBadWordsCache();
        }
    }
}
