package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.AvatarUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class AvatarUploadServiceImpl implements AvatarUploadService {
    @Value("${upload.avatar.path}")
    private String avatarUploadDir;
    @Autowired
    private UserMapper userMapper;

    // 头像上传处理
    @Override
    public String uploadAvatar(Integer userId, MultipartFile file) throws IOException {
        // 根据userId查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return "用户不存在";
        }

        // 获取文件扩展名并验证
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!isValidImageExtension(fileExtension)) {
            return "无效的图片类型";
        }

        // 处理旧头像删除
        String oldAvatar = user.getAvatar();
        if (oldAvatar != null && !oldAvatar.trim().isEmpty() && !oldAvatar.equals("default.webp")) {
            Path avatarDir = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
            Path oldPath = avatarDir.resolve(oldAvatar).normalize();

            // 安全检查：确保旧路径在指定目录下
            if (oldPath.startsWith(avatarDir)) {
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    log.error("删除旧头像失败: {}", oldPath, e);
                    // 继续上传新文件，不终止流程
                }
            } else {
                log.warn("检测到非法路径: {}", oldPath);
            }
        }

        // 创建上传目录（如果不存在）
        Path uploadDir = Paths.get(avatarUploadDir);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一的新文件路径
        Path targetLocation = getUniqueFilePath(avatarUploadDir, fileExtension);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 获取生成的文件名（包括后缀）
        String newFileName = targetLocation.getFileName().toString();

        // 获取相对路径并更新数据库

        user.setAvatar(newFileName);
        userMapper.updateById(user);

        return "图片上传成功";
    }

    // 获取文件扩展名
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    // 验证图片文件扩展名
    private boolean isValidImageExtension(String extension) {
        // 将扩展名转为小写，并判断是否符合要求
        String lowerCaseExtension = extension.toLowerCase();
        return lowerCaseExtension.equals("jpg") || lowerCaseExtension.equals("jpeg") || lowerCaseExtension.equals("png");
    }

    // 使用UUID生成唯一的文件保存路径
    private Path getUniqueFilePath(String uploadDir, String fileExtension) {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + fileExtension;
        return Paths.get(uploadDir, fileName);
    }
}
