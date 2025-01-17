package com.cheering.report.memberReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.meet.Meet;
import com.cheering.meet.MeetRepository;
import com.cheering.post.Post;
import com.cheering.report.postReport.PostReport;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberReportService {

    private final FanRepository fanRepository;
    private final MeetRepository meetRepository;
    private final MemberReportRepository memberReportRepository;

    @Transactional
    public void reportMember(MemberReportRequest.MeetMemberReportRequest request, User user) {

        Meet meet = meetRepository.findById(request.meetId())
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(meet.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Fan reportedFan = fanRepository.findById(request.reportedFanId()).orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        Optional<MemberReport> report = memberReportRepository.findByReportedFanAndWriter(reportedFan, curFan);

        if(report.isPresent()) {
            return;
        }

        MemberReport newMemberReport = MemberReport.builder()
                .reportedFan(reportedFan)
                .writer(curFan)
                .userId(reportedFan.getId())
                .reportReason(request.reason())
                .meet(meet)
                .build();

        memberReportRepository.save(newMemberReport);
    }
}
