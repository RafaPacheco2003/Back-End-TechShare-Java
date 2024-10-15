package com.techmate.techmate.ImageStorage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageStrategy {
    String saveImage(MultipartFile image);
}
