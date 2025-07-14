package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UserInfo;
import com.example.demo.entity.User;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    @Autowired  //将实例化对象Service自动注入
    private UserInfoService userInfoService;

    @GetMapping("/getUserInfo")
    public Result<User> getUserInfo(
            @RequestAttribute Integer userId // 从拦截器中获取的 userId
    ) {
        User user = userInfoService.getUserInfo(userId);
        return Result.success("用户信息获取成功",user);
    }

    @GetMapping("/getOtherUserInfo")
    public Result<User> getOtherUserInfo(@RequestParam Integer userId) {
        try {
            // 获取用户信息
            User user = userInfoService.getUserInfo(userId);
            return Result.success("用户信息获取成功", user);
        } catch (IllegalArgumentException e) {
            // 用户不存在，返回失败相应
            return Result.failure("用户不存在");
        }
    }

    // 更新用户个性签名方法
    @PostMapping("/updateSignature")
    public Result<User> updateSignature(@RequestAttribute Integer userId, @RequestParam String newSignature) {
        try {
            // 调用服务层更新个性签名
            userInfoService.updateSignature(userId, newSignature);
            return Result.success("签名修改成功"); // 返回更新后的用户对象
        } catch (IllegalArgumentException e) {
            // 返回错误信息
            return Result.failure("签名修改失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.failure("签名修改失败:" + e.getMessage());
        }
    }

    // 更新用户个人公告方法
    @PostMapping("/updateAnnouncement")
    public Result<User> updateAnnouncement(@RequestAttribute Integer userId, @RequestParam String newAnnouncement) {
        try {
            // 调用服务层更新个人公告
            userInfoService.updateAnnouncement(userId, newAnnouncement);
            return Result.success("公告修改成功"); // 返回更新后的用户对象
        } catch (IllegalArgumentException e) {
            // 返回错误信息
            return Result.failure("公告修改失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.failure("公告修改失败:" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId   用户id
     * @param userInfo 用户信息
     * @return 更新后的用户对象
     */
    @PutMapping("/updateUserInfo")
    public Result<User> updateUserInfo(@RequestAttribute Integer userId, @RequestBody UserInfo userInfo) {
        System.out.println(userInfo);
        try {
            User updatedUser = userInfoService.updateUserInfo(userId, userInfo);
            if (updatedUser != null) {
                return Result.success("个人信息更新成功", updatedUser);
            } else {
                return Result.failure("个人信息更新失败");
            }
        } catch (Exception e) {
            return Result.failure("更新用户信息时发生错误: " + e.getMessage());
        }
    }
}
