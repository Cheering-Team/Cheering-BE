package com.cheering.apply;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final ApplyRepository applyRepository;

    public void applyCommunity(ApplyRequest.ApplyCommunityDTO requestDTO, User user) {
        Apply apply = Apply.builder()
                .content(requestDTO.content())
                .writer(user)
                .status(ApplyStatus.PENDING)
                .build();

        applyRepository.save(apply);
    }

    public List<ApplyResponse.ApplyDTO> getCommunityApplies(User user) {
        List<Apply> applies = applyRepository.findByWriter(user);
        return applies.stream().map(ApplyResponse.ApplyDTO::new).toList();
    }

    @Transactional
    public void deleteApply(Long applyId) {
        Apply apply = applyRepository.findById(applyId).orElseThrow(()->new CustomException(ExceptionCode.APPLY_NOT_FOUND));

        if(!apply.getStatus().equals(ApplyStatus.PENDING)) {
            throw new CustomException(ExceptionCode.ALREADY_HANDLED_APPLY);
        }

        applyRepository.delete(apply);
    }
}
