package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.AvatarUploadService;
import com.example.demo.service.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class AvatarUploadController {

    @Autowired
    private AvatarUploadService avatarUploadService;

    /**
     * 头像上传接口
     *
     * @param userId 用户ID
     * @param file   上传的头像文件
     * @return Result 类型的响应
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestAttribute Integer userId,
                                       @RequestParam("file") MultipartFile file) {
        try {
            // 调用上传头像的服务方法
            String result = avatarUploadService.uploadAvatar(userId, file);
            if (result.equals("用户不存在")) {
                // 用户不存在时返回失败的 Result
                return Result.failure("用户不存在");
            } else if (result.equals("无效的图片类型")) {
                // 文件类型无效时返回失败的 Result
                return Result.failure("无效的图片类型");
            } else {
                // 上传成功时返回成功的 Result
                return Result.success("头像上传成功");
            }
        } catch (IOException e) {
            // 捕获文件上传过程中的异常，返回失败的 Result
            return Result.failure("文件上传失败");
        }
    }
}
