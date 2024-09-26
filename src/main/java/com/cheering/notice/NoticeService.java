package com.cheering.notice;

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
}
