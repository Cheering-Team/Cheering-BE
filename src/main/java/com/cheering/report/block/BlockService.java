package com.cheering.report.block;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final PlayerUserRepository playerUserRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public void blockUser(Long playerUserId, User user) {
        PlayerUser to = playerUserRepository.findById(playerUserId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));
        PlayerUser from = playerUserRepository.findByPlayerIdAndUserId(to.getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Block block = Block.builder()
                .to(to)
                .from(from)
                .build();

        blockRepository.save(block);

        // 차단한 후 해당 유저가 만든 채팅방에서 자동으로 나가짐
        chatSessionRepository.deleteByChatRoomCreatorAndCurPlayerUser(to, from);
    }
}
