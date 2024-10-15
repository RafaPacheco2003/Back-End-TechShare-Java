package com.techmate.techmate.ImageStorage.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.ImageStorage.ImageStorageStrategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Component
public class FileSystemImageStorage implements ImageStorageStrategy{

    @Value("${storage.location}")
    private String storageLocation;

     @Override
    public String saveImage(MultipartFile image) {
        Path path = Paths.get(storageLocation, image.getOriginalFilename());
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        return image.getOriginalFilename();
    }
    
}
