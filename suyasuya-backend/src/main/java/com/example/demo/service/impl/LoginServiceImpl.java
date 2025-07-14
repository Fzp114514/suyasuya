package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CaptchaService;
import com.example.demo.service.LoginService;
import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl extends ServiceImpl<UserMapper, User> implements LoginService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String loginByPassword(String email, String password) {
        User user = userMapper.selectByEmail(email);
        // 检查邮箱是否存在
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 3. 生成JWT
        return jwtUtil.generateToken(user.getUserId());
    }

    @Override
    public String loginByCaptcha(String email, String captcha) {
        // 检查邮箱是否存在（由CaptchaService的发送逻辑保证）
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        // 验证验证码
        if (!captchaService.verifyCaptcha("login:email:captcha:" + email, captcha)) {
            throw new RuntimeException("验证码错误");
        }
        // 登录成功
        return jwtUtil.generateToken(user.getUserId());
    }
}
