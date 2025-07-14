// 此处用于存放所有登录模块相关的接口请求

import request from "@/utils/request"

export const getLoginCaptcha = email =>
    request.post(`/captcha/send-login-captcha?email=${email}`)

export const loginByPassword = (email, password) =>
    request.post(`/login/loginByPassword?email=${email}&password=${password}`)

export const loginByCaptcha = (email, captcha) =>
    request.post(`/login/loginByCaptcha?email=${email}&captcha=${captcha}`)