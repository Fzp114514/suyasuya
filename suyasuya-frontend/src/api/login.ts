// 此处用于存放所有登录模块相关的接口请求

import apiRequest from "@/utils/request"

export const getLoginCaptcha = (email: string) =>
    apiRequest.post(`/captcha/send-login-captcha?email=${email}`)

export const loginByPassword = (email: string, password: string) =>
    apiRequest.post(`/login/loginByPassword?email=${email}&password=${password}`)

export const loginByCaptcha = (email: string, captcha: string) =>
    apiRequest.post(`/login/loginByCaptcha?email=${email}&captcha=${captcha}`)