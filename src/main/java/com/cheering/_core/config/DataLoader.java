package com.cheering._core.config;

import com.cheering.chat.ChatRoom.ChatRoom;
import com.cheering.chat.ChatRoom.ChatRoomRepository;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReComment;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostRepository;
import com.cheering.post.Tag.Tag;
import com.cheering.post.Tag.TagRepository;
import com.cheering.post.relation.PostTag;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
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
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerUserRepository playerUserRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final PostImageRepository postImageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user1 = User.builder()
                .phone("01062013112")
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
                .fanCount(123512L)
                .build();

        Team hanwha = Team.builder()
                .league(kbo)
                .name("한화 이글스")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/hanwha.jpeg")
                .fanCount(123512L)
                .build();

        Team samsung = Team.builder()
                .league(kbo)
                .name("삼성 라이온즈")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/samsung.jpeg")
                .fanCount(123512L)
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

        Player player1 = Player.builder()
                .koreanName("황성빈")
                .englishName("HWANG SEONG-BIN")
                .image("https://file.giantsclub.com/upload2014/Player/50500_s.jpg")
                .backgroundImage("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/674350_1031929_1335.jpg")
                .build();

        Player player2 = Player.builder()
                .koreanName("전준우")
                .englishName("JEON JUN-WOO")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%8C%E1%85%A5%E1%86%AB%E1%84%8C%E1%85%AE%E1%86%AB%E1%84%8B%E1%85%AE.jpeg")
                .backgroundImage("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%8C%E1%85%A5%E1%86%AB%E1%84%8C%E1%85%AE%E1%86%AB%E1%84%8B%E1%85%AE+%E1%84%87%E1%85%A2%E1%84%80%E1%85%A7%E1%86%BC.jpeg")
                .build();

        Player player3 = Player.builder()
                .koreanName("이민우")
                .englishName("LEE MIN-WOO")
                .image("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%86%AB%E1%84%8B%E1%85%AE.jpeg")
                .backgroundImage("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%86%AB%E1%84%8B%E1%85%AE%E1%84%87%E1%85%A2%E1%84%80%E1%85%A7%E1%86%BC.jpg")
                .build();

        playerRepository.save(player1);
        playerRepository.save(player2);
        playerRepository.save(player3);


        ChatRoom chatRoom1 = ChatRoom.builder()
                .name("황성빈")
                .description("황성빈 팬들끼리 응원해요!")
                .player(player1)
                .build();

        chatRoomRepository.save(chatRoom1);


        ChatRoom chatRoom2 = ChatRoom.builder()
                .name("전준우")
                .description("전준우 팬들끼리 응원해요!")
                .player(player2)
                .build();

        chatRoomRepository.save(chatRoom2);

        TeamPlayer teamPlayer1 = TeamPlayer.builder()
                .team(lotte)
                .player(player1)
                .build();

        TeamPlayer teamPlayer2 = TeamPlayer.builder()
                .team(lotte)
                .player(player2)
                .build();

        TeamPlayer teamPlayer3 = TeamPlayer.builder()
                .team(hanwha)
                .player(player3)
                .build();

        teamPlayerRepository.save(teamPlayer1);
        teamPlayerRepository.save(teamPlayer2);
        teamPlayerRepository.save(teamPlayer3);

        PlayerUser playerUser = PlayerUser.builder()
                .player(player1)
                .user(user1)
                .nickname("황성빈짱")
                .build();

        playerUserRepository.save(playerUser);

        Tag tag1 = Tag.builder()
                .name("photo")
                .build();

        Tag tag2 = Tag.builder()
                .name("information")
                .build();

        Tag tag3 = Tag.builder()
                .name("viewing")
                .build();

        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);

        Post post1 = Post.builder()
                .playerUser(playerUser)
                .content("1번 글")
                .build();

        Post post2 = Post.builder()
                .playerUser(playerUser)
                .content("2번 글")
                .build();

        Post post3 = Post.builder()
                .playerUser(playerUser)
                .content("3번 글")
                .build();

        Post post4 = Post.builder()
                .playerUser(playerUser)
                .content("4번 글")
                .build();

        Post post5 = Post.builder()
                .playerUser(playerUser)
                .content("5번 글")
                .build();

        Post post6 = Post.builder()
                .playerUser(playerUser)
                .content("6번 글")
                .build();

        Post post7 = Post.builder()
                .playerUser(playerUser)
                .content("7번 글")
                .build();

        Post post8 = Post.builder()
                .playerUser(playerUser)
                .content("8번 글")
                .build();

        Post post9 = Post.builder()
                .playerUser(playerUser)
                .content("9번 글")
                .build();

        Post post10 = Post.builder()
                .playerUser(playerUser)
                .content("황성빈 보고싶다")
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);
        postRepository.save(post6);
        postRepository.save(post7);
        postRepository.save(post8);
        postRepository.save(post9);
        postRepository.save(post10);

        PostTag postTag1 = PostTag.builder()
                .tag(tag1)
                .post(post10)
                .build();

        postTagRepository.save(postTag1);

        PostImage postImage1 = PostImage.builder()
                .post(post10)
                .width(1024)
                .height(1374)
                .path("https://imgnews.pstatic.net/image/109/2022/06/28/0004646589_001_20220628193005455.jpg")
                .build();

        PostImage postImage2 = PostImage.builder()
                .post(post10)
                .width(1021)
                .height(1361)
                .path("https://search.pstatic.net/common/?src=http%3A%2F%2Fimgnews.naver.net%2Fimage%2F109%2F2022%2F06%2F01%2F0004627413_001_20220601180505442.jpg")
                .build();

        postImageRepository.save(postImage1);
        postImageRepository.save(postImage2);

        Comment comment1 = Comment.builder()
                .post(post10)
                .content("댓글1")
                .playerUser(playerUser)
                .build();

        commentRepository.save(comment1);

        ReComment reComment1 = ReComment.builder()
                .comment(comment1)
                .content("답글1")
                .playerUser(playerUser)
                .toPlayerUser(playerUser)
                .build();

        reCommentRepository.save(reComment1);
    }
}
