package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.CaptchaService;
import com.example.demo.utils.EmailApi;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CaptchaController {
    @Resource
    private CaptchaService captchaService;

    // 前端提交邮箱地址来获取验证码
    // 发送注册验证码功能
    @PostMapping("/captcha/send-register-captcha")
    public Result<Void> sendRegisterCaptcha(@RequestParam String email) {
        try {
            boolean success = captchaService.sendRegisterCaptcha(email); // 调用服务发送验证码
            if (success) {
                return Result.success("验证码发送成功"); // 发送成功时，返回成功结果
            } else {
                return Result.failure("验证码发送失败"); // 发送失败时，返回失败结果
            }
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage()); // 捕获异常并返回失败结果
        }
    }

    // 发送登录验证码功能
    @PostMapping("/captcha/send-login-captcha")
    public Result<Void> sendLoginCaptcha(@RequestParam String email) {
        try {
            boolean success = captchaService.sendLoginCaptcha(email); // 调用服务发送验证码
            if (success) {
                return Result.success("验证码发送成功"); // 发送成功时，返回成功结果
            } else {
                return Result.failure("验证码发送失败"); // 发送失败时，返回失败结果
            }
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage()); // 捕获异常并返回失败结果
        }
    }
}
