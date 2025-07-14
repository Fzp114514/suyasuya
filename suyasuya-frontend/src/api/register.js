// 此处用于存放所有注册模块相关的接口请求

import request from "@/utils/request"

export const getRegisterCaptcha = email =>
    request.post(`/captcha/send-register-captcha?email=${email}`)

export const codeRegister = (username, password, email, captcha) =>
    request.post('/register', {
        username,
        password,
        email,
        captcha
    })