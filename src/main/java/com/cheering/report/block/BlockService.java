package com.cheering.report.block;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final FanRepository fanRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public void blockFan(Long fanId, User user) {
        Fan to = fanRepository.findById(fanId).orElseThrow(()-> new CustomException(ExceptionCode.FAN_NOT_FOUND));
        Fan from = fanRepository.findByCommunityIdAndUser(to.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Block block = Block.builder()
                .to(to)
                .from(from)
                .build();

        blockRepository.save(block);

        // 차단한 후 해당 유저가 만든 채팅방에서 자동으로 나가짐
        chatSessionRepository.deleteByChatRoomManagerAndCurFan(to, from);
    }

    public List<FanResponse.FanDTO> getBlockedFans(Long fanId) {
        Fan fan = fanRepository.findById(fanId).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
        List<Fan> fans = blockRepository.findToByFrom(fan);

        return fans.stream().map((FanResponse.FanDTO::new)).toList();
    }

    @Transactional
    public void unblockFan(Long fanId, User user) {
        Fan fan = fanRepository.findById(fanId).orElseThrow(()-> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(fan.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        blockRepository.deleteByFromAndTo(curFan, fan);
    }
}
