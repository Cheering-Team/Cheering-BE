package com.cheering.notice.apply;

import com.cheering._core.util.S3Util;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    public void apply(String field1, String field2, String field3, String field4, MultipartFile image, User user) {
        String imageUrl = "";

        if(image != null) {
            imageUrl = s3Util.upload(image);
        }

        Apply apply = Apply.builder()
                .writer(user)
                .field1(field1)
                .field2(field2)
                .field3(field3)
                .field4(field4)
                .image(imageUrl)
                .build();

        applyRepository.save(apply);
    }
}
