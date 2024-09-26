package com.cheering.notice;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    public List<NoticeResponse.NoticeDTO> getNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return notices.stream().map(NoticeResponse.NoticeDTO::new).toList();
    }

    public NoticeResponse.NoticeDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(()-> new CustomException(ExceptionCode.NOTICE_NOT_FOUND));

        return new NoticeResponse.NoticeDTO(notice);
    }
}
