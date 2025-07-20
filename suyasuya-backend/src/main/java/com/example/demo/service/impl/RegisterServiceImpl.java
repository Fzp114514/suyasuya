package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.UserCollection;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserCollectionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.RegisterService;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class RegisterServiceImpl extends ServiceImpl<UserMapper, User> implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserCollectionMapper userCollectionMapper; // 新增收藏夹Mapper
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void register(RegisterRequest request) {
        // 检查邮箱是否已注册
        if (userInfoService.emailExists(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 加密密码并创建用户
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userMapper.insert(user);

        // 创建默认收藏夹
        createDefaultCollection(user.getUserId());
    }

    // 创建默认收藏夹方法
    private void createDefaultCollection(Integer userId) {
        UserCollection userCollection = new UserCollection();
        userCollection.setUserId(userId);
        userCollection.setCollectionName("默认收藏夹");
        userCollection.setIsDefault(true); // 设置默认标识
        userCollection.setCreateTime(new Date());
        userCollection.setUpdateTime(new Date());
        userCollection.setVideoCount(0);
        userCollectionMapper.insert(userCollection);
    }
}
