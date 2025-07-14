package com.example.demo.enums;

public enum EmailTemplateEnum {
    REGISTRATION("注册验证", "<h2>欢迎注册！您的验证码是：{0}</h2>"),
    LOGIN("登录验证", "<h2>您的登录验证码是：{0}</h2>");

    private final String subject;
    private final String htmlTemplate;

    // 构造函数
    EmailTemplateEnum(String subject, String htmlTemplate) {
        this.subject = subject;
        this.htmlTemplate = htmlTemplate;
    }

    // 获取邮件主题
    public String getSubject() {
        return subject;
    }

    // 设置验证码
    public String set(String captcha) {
        return htmlTemplate.replace("{0}", captcha);
    }
}