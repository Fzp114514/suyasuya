package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarUploadService {
    String uploadAvatar(Integer userId, MultipartFile image) throws IOException;
}
