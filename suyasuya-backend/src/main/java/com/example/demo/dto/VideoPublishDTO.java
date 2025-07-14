package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 数据传输对象
@Data
public class VideoPublishDTO {
    @NotNull
    private Integer videoId;

    @NotNull
    private MultipartFile coverFile;

    @NotBlank
    private String title;

    @NotNull
    private Integer categoryId;

    private List<String> tags;

    private String introduction;
}