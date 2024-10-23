package com.cheering.post.PostImage;

public class PostImageResponse {
    public record ImageDTO(String path, int width, int height, PostImageType type) {
        public ImageDTO(PostImage postImage) {
            this(postImage.getPath(), postImage.getWidth(), postImage.getHeight(), postImage.getType());
        }
    }
}
