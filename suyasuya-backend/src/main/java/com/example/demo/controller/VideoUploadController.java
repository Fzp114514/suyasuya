package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.VideoPublishDTO;
import com.example.demo.service.VideoUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class VideoUploadController {
    @Autowired
    private VideoUploadService videoUploadService;

    /**
     * 临时视频上传接口
     * @param userId 作者id
     * @param file 视频文件
     */
    @PostMapping("/tempVideo")
    public Result<Map<String, Object>> uploadTemporary(@RequestAttribute Integer userId,
                                                       @RequestParam("file") MultipartFile file) {
        try {
            // 基础校验
            if (userId == null || userId <= 0) {
                return Result.failure("作者ID无效");
            }
            if (file.isEmpty()) {
                return Result.failure("上传文件不能为空");
            }

            // 调用服务层上传逻辑
            Map<String, Object> uploadResult = videoUploadService.uploadVideoTemporary(userId, file);
            return Result.success("视频上传成功", uploadResult);

        } catch (IllegalArgumentException e) {
            // 参数校验类异常（如文件类型错误）
            return Result.failure(e.getMessage());
        } catch (RuntimeException e) {
            // 业务逻辑异常（如用户不存在、文件保存失败）
            return Result.failure("视频上传失败: " + e.getMessage());
        } catch (Exception e) {
            // 其他未知异常
            log.error("搜索服务异常：",e);
            return Result.failure("系统错误，请稍后重试"+e.getMessage());
        }
    }

    /**
     * 正式投稿接口
     * @param dto 投稿数据
     */
    @PostMapping("/publish")
    public Result publishVideo(
            @Validated @ModelAttribute VideoPublishDTO dto) {
        try {
            // 业务处理
            videoUploadService.publishVideo(dto);
            return Result.success("视频投稿成功");

        } catch (RuntimeException e) {
            return Result.failure( e.getMessage());
        } catch (Exception e) {
            return Result.failure("投稿处理失败");
        }
    }
}
