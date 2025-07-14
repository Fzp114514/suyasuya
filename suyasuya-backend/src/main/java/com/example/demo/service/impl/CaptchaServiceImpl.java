package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.enums.EmailTemplateEnum;
import com.example.demo.service.CaptchaService;
import com.example.demo.service.UserInfoService;
import com.example.demo.utils.EmailApi;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private UserInfoService userInfoService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    EmailApi emailApi;

    @Override
    public boolean sendRegisterCaptcha(String email) {
        // 先检查邮箱是否已经注册
        if (userInfoService.emailExists(email)) {
            throw new RuntimeException("该邮箱已注册");  // 如果已注册，则抛出异常
        }
        // 调用发送验证码的逻辑
        sendMailCaptcha("register:email:captcha:" + email);
        return true;
    }

    @Override
    public boolean sendLoginCaptcha(String email) {
        // 先检查邮箱是否已经注册
        if (!userInfoService.emailExists(email)) {
            throw new RuntimeException("该邮箱未注册");  // 如果未注册，则抛出异常
        }
        sendMailCaptcha("login:email:captcha:" + email);
        return true;
    }

    // 校验验证码
    @Override
    public boolean verifyCaptcha(String key, String captcha) {
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(key);
        String storedCaptcha = hashOps.get("captcha");

        // 检查从 Redis 中获取到的验证码 storedCaptcha 是否为空或仅由空格组成
        if (StringUtils.isBlank(storedCaptcha)) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 比对验证码
        if (!storedCaptcha.equals(captcha)) {
            return false; // 验证失败
        }

        // 验证成功后，删除验证码
        hashOps.delete("captcha");
        return true;
    }

//    ================ 发送验证码 ==================

    private boolean sendMailCaptcha(String key) {
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(key);
        // 初始检查
        String lastSendTimestamp = hashOps.get("lastSendTimestamp");        // 记录上次发送验证码的时间戳
        String sendCount = hashOps.get("sendCount");                        // 记录验证码发送次数

        // 如果发送次数超过 5 次，则抛出异常并限制 24 小时内不能发送
        if (StringUtils.isNotBlank(sendCount) && Integer.parseInt(sendCount) >= 5) {
            hashOps.expire(24, TimeUnit.HOURS);
            throw new RuntimeException("验证码发送过于频繁,封禁一天");
        }
        // 如果两次发送时间间隔小于 1 分钟，抛出异常，限制频繁请求
        if (StringUtils.isNotBlank(lastSendTimestamp)) {
            long lastSendTime = Long.parseLong(lastSendTimestamp);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastSendTime;
            if (elapsedTime < 60 * 1000) {
                throw new RuntimeException("验证码发送过于频繁");
            }
        }
        int newSendCount = StringUtils.isNotBlank(sendCount) ? Integer.parseInt(sendCount) + 1 : 1;
        String captcha = RandomStringUtils.randomNumeric(6);

        try {
            sendCaptcha(key, captcha);
        } catch (Exception e) {
            return false;
        }
        hashOps.put("captcha", captcha);
        hashOps.put("lastSendTimestamp", String.valueOf(System.currentTimeMillis()));
        hashOps.put("sendCount", String.valueOf(newSendCount));
        hashOps.expire(5, TimeUnit.MINUTES); // 设置过期时间为5分钟

        return true;
    }

    // 邮件发送逻辑
    private void sendCaptcha(String hashKey, String captcha) throws Exception {
        // 根据hashKey判断是发送邮件还是短信，然后调用相应的发送方法
        String[] keyParts = hashKey.split(":");
        String type = keyParts[0];  // "register" 或 "login"
        String email = keyParts[3]; // 提取邮箱部分

        if ("email".equals(keyParts[1])) {
            String subject = "";
            String content = "";

            if ("register".equals(type)) {
                // 注册时发送的邮件内容
                subject = EmailTemplateEnum.REGISTRATION.getSubject();
                content = EmailTemplateEnum.REGISTRATION.set(captcha);
            } else if ("login".equals(type)) {
                // 登录时发送的邮件内容
                subject = EmailTemplateEnum.LOGIN.getSubject();
                content = EmailTemplateEnum.LOGIN.set(captcha);
            }

            // 发送邮件
            if (!emailApi.sendHtmlEmail(subject, content, email)) {
                throw new RuntimeException("发送邮件失败");
            }

        }
    }
}
