package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.UserInfo;
import com.example.demo.entity.User;
import com.example.demo.entity.Video;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 如果有额外的业务逻辑，可以在这里实现
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserMapper, User> implements UserInfoService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoMapper videoMapper;

    // 获取用户个人信息方法
    @Override
    public User getUserInfo(Integer userId) {
        // 从数据库中获取用户
        User user = userMapper.selectByUserId(userId);
        if (user != null) {
            // 移除密码字段和邮箱字段
            user.setPassword(null); //通过设置 null 排除密码字段
            user.setEmail(null);
            return user;
        }
        throw new IllegalArgumentException("用户不存在");
    }

    /**
     * 更新用户的个性签名
     *
     * @param userId       用户id
     * @param newSignature 新的个性签名
     * @return 更新后的用户对象
     */
    @Override
    public User updateSignature(Integer userId, String newSignature) {
        return updateUserField(userId, newSignature, "signature");
    }

    /**
     * 更新用户的个人公告
     *
     * @param userId          用户id
     * @param newAnnouncement 新的公告
     * @return 更新后的用户对象
     */
    @Override
    public User updateAnnouncement(Integer userId, String newAnnouncement) {
        return updateUserField(userId, newAnnouncement, "announcement");
    }

    @Override
    public boolean emailExists(String email) {
        // 根据邮箱查询数据库中是否有对应的用户
        User existingUser = userMapper.selectByEmail(email);
        return existingUser != null;
    }

    /**
     * 批量更新用户信息
     *
     * @param userId   用户id
     * @param userInfo 用户信息
     * @return 更新后的用户对象
     */
    @Transactional  // 添加事务管理
    @Override
    public User updateUserInfo(Integer userId, UserInfo userInfo) {
        // 根据userId查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        boolean userNameChanged = false;
        String newUserName = null;
        // 遍历并更新每个字段
        if (userInfo.getUserName() != null && !userInfo.getUserName().equals(user.getUserName())) {
            newUserName = userInfo.getUserName();
            updateUserField(userId, newUserName, "userName");
            userNameChanged = true;
        }

        if (userInfo.getBirthday() != null && !userInfo.getBirthday().equals(user.getBirthday())) {
            updateUserField(userId, userInfo.getBirthday(), "birthday");
        }

        if (userInfo.getGender() != null && !userInfo.getGender().equals(user.getGender())) {
            updateUserField(userId, userInfo.getGender(), "gender");
        }

        if (userInfo.getSignature() != null && !userInfo.getSignature().equals(user.getSignature())) {
            updateUserField(userId, userInfo.getSignature(), "signature");
        }

        if (userInfo.getAnnouncement() != null && !userInfo.getAnnouncement().equals(user.getAnnouncement())) {
            updateUserField(userId, userInfo.getAnnouncement(), "announcement");
        }
        // 如果用户名变更，同步更新视频表
        if (userNameChanged) {
            videoMapper.updateAuthorNameByUserId(userId, newUserName);
        }
        return userMapper.selectById(userId);   //返回最新数据
    }

    /**
     * 更新用户的任意字段
     *
     * @param userId   用户id
     * @param newValue 新的字段值
     * @param field    需要更新的字段名（例如 signature、announcement、nickname 等）
     * @return 更新后的用户对象
     */
    private User updateUserField(Integer userId, String newValue, String field) {
        // 根据userId查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 获取用户当前字段的值
        String currentFieldValue = getFieldValue(user, field);

        // 如果字段值与新值相同，抛出异常
        if (currentFieldValue != null && currentFieldValue.equals(newValue)) {
            throw new IllegalArgumentException("新的" + field + "与原值相同，无需更新");
        }

        // 更新字段值
        updateField(user, newValue, field);

        // 更新数据库
        userMapper.updateById(user);
        return user;
    }

    /**
     * 根据字段名获取用户相应的字段值
     */
    private String getFieldValue(User user, String field) {
        return switch (field) {
            case "userName" -> user.getUserName();
            case "birthday" -> user.getBirthday();
            case "gender" -> user.getGender();
            case "signature" -> user.getSignature();
            case "announcement" -> user.getAnnouncement();
            // 添加更多字段...
            default -> throw new IllegalArgumentException("未知字段：" + field);
        };
    }

    /**
     * 根据字段名设置用户相应的字段值
     */
    private void updateField(User user, String newValue, String field) {
        switch (field) {
            case "userName":
                user.setUserName(newValue);
                break;
            case "birthday":
                user.setBirthday(newValue);
                break;
            case "gender":
                user.setGender(newValue);
                break;
            case "signature":
                user.setSignature(newValue);
                break;
            case "announcement":
                user.setAnnouncement(newValue);
                break;
            // 添加更多字段...
            default:
                throw new IllegalArgumentException("未知字段：" + field);
        }
    }
}
