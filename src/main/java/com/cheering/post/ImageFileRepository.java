package com.cheering.post;

import com.cheering.post.ImageFile;
import com.cheering.post.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByPost(Post post);
}
