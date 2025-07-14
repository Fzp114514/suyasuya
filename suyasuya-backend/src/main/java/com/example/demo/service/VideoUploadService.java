package com.example.demo.service;

import com.example.demo.dto.VideoPublishDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface VideoUploadService {
    Map<String, Object> uploadVideoTemporary(Integer authorId, MultipartFile file);
    void publishVideo(VideoPublishDTO publishDTO) throws IOException;
}
