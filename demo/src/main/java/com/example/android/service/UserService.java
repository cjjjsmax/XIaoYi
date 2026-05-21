package com.example.android.service;

import com.example.android.entity.User;
import com.example.android.mapper.UserMapper;
import com.example.android.utils.JwtUtil;
import com.example.android.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //用户注册
    public boolean register(User user) throws Exception {
        User existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser != null) {
            throw new Exception("用户名已存在");
        }
        User existingStudentId = userMapper.selectByStudentId(user.getStudentId());
        if (existingStudentId != null) {
            throw new Exception("学号已被注册");
        }
        if (user.getPasswordHash() == null) {
            throw new Exception("密码不能为空");
        }
        user.setPasswordHash(PasswordUtil.encodePassword(user.getPasswordHash()));
        if (user.getCreditScore() == null) {
            user.setCreditScore(100);
        }
        Date now = new Date();
        user.setCreatedAt(now);
        int result = userMapper.insert(user);
        return result > 0;
    }
    //用户登录
    public Map<String, Object> login(String username, String password) throws Exception {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new Exception("用户名或密码错误");
        }
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new Exception("用户名或密码错误");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.generateToken(claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        user.setPasswordHash(null);
        result.put("user", user);
        return result;
    }
    //根据id获取用户
    public User getUserById(Long userId){
        return userMapper.selectById(userId);
    }
    //获取所有用户
    public java.util.List<User> getAllUsers(){
        return userMapper.selectAllUsers();
    }
    //修改密码
    public boolean changePassword(Long userId, String oldPassword, String newPassword) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        if (!PasswordUtil.matches(oldPassword, user.getPasswordHash())) {
            throw new Exception("旧密码错误");
        }
        String encodedPassword = PasswordUtil.encodePassword(newPassword);
        int result = userMapper.updatePassword(userId, encodedPassword);
        return result > 0;
    }
    //删除用户
    public boolean deleteUser(Long userId) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        int result = userMapper.deleteById(userId);
        return result > 0;
    }
    //更新用户信用分
    public boolean updateCreditScore(Long userId, Integer creditScore) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        if (creditScore < 0){
            creditScore = 0;
        } else if (creditScore > 100){
            creditScore = 100;
        }
        int result = userMapper.updateCreditScore(userId, creditScore);
        return result > 0;
    }
    //更新用户信息
    public boolean updateUser(User user) throws Exception {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new Exception("用户不存在");
        }
        int result = userMapper.updateUser(user);
        return result > 0;
    }
}
