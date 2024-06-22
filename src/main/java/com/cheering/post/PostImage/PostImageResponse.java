package com.cheering.post.PostImage;

public class PostImageResponse {
    public record ImageDTO(String url, int width, int height) {
        public ImageDTO(PostImage postImage) {
            this(postImage.getPath(), postImage.getWidth(), postImage.getHeight());
        }
    }
}
