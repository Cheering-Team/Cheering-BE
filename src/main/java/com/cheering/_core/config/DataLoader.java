package com.cheering._core.config;

import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.sport.Sport;
import com.cheering.team.sport.SportRepository;
import com.cheering.user.Role;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private final UserRepository userRepository;
    private final SportRepository sportRepository;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user1 = User.builder()
                .phone("01062013110")
                .nickname("준서")
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user1);

        Sport baseball = Sport.builder()
                .name("야구")
                .build();

        sportRepository.save(baseball);

        League kbo = League.builder()
                .name("KBO")
                .sport(baseball)
                .build();

        League mlb = League.builder()
                .name("MLB")
                .sport(baseball)
                .build();

        leagueRepository.save(kbo);
        leagueRepository.save(mlb);

        Team lotte = Team.builder()
                .league(kbo)
                .name("롯데 자이언츠")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/lotte.png")
                .build();

        Team hanwha = Team.builder()
                .league(kbo)
                .name("한화 이글스")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/hanwha.jpeg")
                .build();

        Team samsung = Team.builder()
                .league(kbo)
                .name("삼성 라이온즈")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/samsung.jpeg")
                .build();

        teamRepository.save(lotte);
        teamRepository.save(hanwha);
        teamRepository.save(samsung);

        Sport soccer = Sport.builder()
                .name("축구")
                .build();

        sportRepository.save(soccer);

        League kleague = League.builder()
                .name("K리그")
                .sport(soccer)
                .build();

        League epl = League.builder()
                .name("EPL")
                .sport(soccer)
                .build();

        leagueRepository.save(kleague);
        leagueRepository.save(epl);

        Sport basketball = Sport.builder()
                .name("농구")
                .build();

        sportRepository.save(basketball);
    }
}
