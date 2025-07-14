package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.CaptchaService;
import com.example.demo.service.RegisterService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired  //将实例化对象Service自动注入
    private RegisterService registerService;
    @Resource
    private CaptchaService captchaService;

    // 用户注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Valid RegisterRequest request) {
        // 验证验证码
        captchaService.verifyCaptcha("register:email:captcha:" + request.getEmail(), request.getCaptcha());

        // 执行注册
        registerService.register(request);
        return Result.success("注册成功");
    }
}
