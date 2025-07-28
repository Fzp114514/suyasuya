// 此处用于存放所有注册模块相关的接口请求
import apiRequest from "@/utils/request"

export const getRegisterCaptcha = (email: string) =>
    apiRequest.post(`/captcha/send-register-captcha?email=${email}`)

export const codeRegister = (username: string, password: string, email: string, captcha: string) =>
    apiRequest.post('/register', {
        username,
        password,
        email,
        captcha
    })