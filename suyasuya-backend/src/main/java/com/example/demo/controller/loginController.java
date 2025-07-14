package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.CaptchaService;
import com.example.demo.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class loginController {
    @Autowired  //将实例化对象Service自动注入
    private LoginService loginService;
    @Resource
    private CaptchaService captchaService;

    // 邮箱+密码登录
    @PostMapping("/loginByPassword")
    public Result<String> loginByPassword(@RequestParam String email, @RequestParam String password) {
        try {
            String token = loginService.loginByPassword(email, password);
            return Result.success("登录成功", token);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }

    // 邮箱+验证码登录
    @PostMapping("/loginByCaptcha")
    public Result<String> loginByCaptcha(@RequestParam String email, @RequestParam String captcha) {
        try {
            String token = loginService.loginByCaptcha(email,captcha);
            return Result.success("登录成功",token);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }
}
