package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.UserInfo;
import com.example.demo.entity.User;

public interface UserInfoService extends IService<User> {
    // 具体业务逻辑
    public boolean emailExists(String email);

    User getUserInfo(Integer userId);

    User updateSignature(Integer userId, String newSignature);

    User updateAnnouncement(Integer userId, String newAnnouncement);
    User updateUserInfo(Integer userId, UserInfo userInfo);
}
