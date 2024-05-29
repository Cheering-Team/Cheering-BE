package com.cheering.team.sport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportService {
    private final SportRepository sportRepository;

    @Transactional
    public List<SportResponse.SportDTO> getSports() {
        List<Sport> sports = sportRepository.findAll();

        return sports.stream().map(SportResponse.SportDTO::new).toList();
    }
}
