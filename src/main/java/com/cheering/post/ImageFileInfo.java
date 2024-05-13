package com.cheering.post;

import java.net.URL;
import java.util.List;
import lombok.Builder;

@Builder
public record ImageFileInfo(
        URL url,
        int width,
        int height
) {
    public static List<ImageFileInfo> ofList(List<ImageFile> imageFiles) {
        return imageFiles.stream().map(imageFile -> ImageFileInfo.builder()
                .url(imageFile.getPath())
                .width(imageFile.getWidth())
                .height(imageFile.getHeight())
                .build()).toList();
    }
}
