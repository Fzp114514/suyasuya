package com.example.demo.service;

public interface CaptchaService {
    boolean sendRegisterCaptcha(String email);
    boolean sendLoginCaptcha(String email);
    boolean verifyCaptcha(String key, String captcha);
}
