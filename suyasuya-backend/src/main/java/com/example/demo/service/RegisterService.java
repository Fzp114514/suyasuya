package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;

public interface RegisterService extends IService<User> {
    void register(RegisterRequest request);
}
