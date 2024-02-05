package com.cheering.domain.post.repository;

import com.cheering.domain.post.domain.ImageFile;
import com.cheering.domain.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByPost(Post post);
}
