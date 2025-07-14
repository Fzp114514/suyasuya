package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.User;

public interface LoginService extends IService<User> {
    String loginByPassword(String email, String password);

    String loginByCaptcha(String email, String captcha);
}
