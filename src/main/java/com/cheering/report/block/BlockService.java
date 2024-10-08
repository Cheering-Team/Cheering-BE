package com.cheering.report.block;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final PlayerUserRepository playerUserRepository;

    public void blockUser(Long playerUserId, User user) {
        PlayerUser to = playerUserRepository.findById(playerUserId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));
        PlayerUser from = playerUserRepository.findByPlayerIdAndUserId(to.getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Block block = Block.builder()
                .to(to)
                .from(from)
                .build();

        blockRepository.save(block);
    }
}
