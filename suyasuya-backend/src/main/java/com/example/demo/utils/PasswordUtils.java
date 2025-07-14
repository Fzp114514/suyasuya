//package com.example.demo.utils;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PasswordUtils {
//    private final PasswordEncoder passwordEncoder;
//
//    public PasswordUtils() {
//        this.passwordEncoder=new BCryptPasswordEncoder();
//    }
//
//    // 加密密码
//    public String encodePassword(String rawPassword) {
//        return passwordEncoder.encode(rawPassword);
//    }
//
//    // 校验密码
//    public boolean matches(String rawPassword, String encodedPassword) {
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }
//}
