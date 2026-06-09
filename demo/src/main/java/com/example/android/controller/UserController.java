package com.example.android.controller;

import com.example.android.common.Result;
import com.example.android.entity.User;
import com.example.android.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (success) {
                return Result.success("注册成功", null);
            } else {
                return Result.badRequest("注册失败");
            }
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            Map<String, Object> loginResult = userService.login(username, password);
            return Result.success("登录成功", loginResult);
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public Result<User> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setPasswordHash(null);//移除敏感信息
                return Result.success("获取成功", user);
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Void> updateUser(
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username,
            @RequestParam("studentId") String studentId,
            @RequestParam("school") String school) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setUsername(username);
                user.setStudentId(studentId);
                user.setSchool(school);
                boolean success = userService.updateUser(user);
                if (success) {
                    return Result.success("更新成功", null);
                } else {
                    return Result.badRequest("更新失败");
                }
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update-security")
    public Result<Void> updateAccountSecurity(
            @RequestParam("userId") Long userId,
            @RequestParam("phone") String phone,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setPhone(phone);
                
                if (oldPassword != null && newPassword != null && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
                    boolean passwordChanged = userService.changePassword(userId, oldPassword, newPassword);
                    if (!passwordChanged) {
                        return Result.badRequest("原密码错误");
                    }
                }
                
                boolean success = userService.updateUser(user);
                if (success) {
                    return Result.success("更新成功", null);
                } else {
                    return Result.badRequest("更新失败");
                }
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, Object> changePasswordData) {
        try {
            Long userId = Long.parseLong(changePasswordData.get("userId").toString());
            String oldPassword = (String) changePasswordData.get("oldPassword");
            String newPassword = (String) changePasswordData.get("newPassword");

            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功", null);
            } else {
                return Result.badRequest("密码修改失败");
            }
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }
    @PostMapping("/upload-avatar")
    public Result<Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            //验证文件是否为空
            if (file.isEmpty()) {
                return Result.badRequest("文件为空");
            }

            //验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.badRequest("只能上传图片文件");
            }

            //生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString() + extension;

            //创建保存目录
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //保存文件
            String filePath = uploadDir + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            //更新用户头像URL
            User user = userService.getUserById(userId);
            if (user != null) {
                //删除旧头像
                String oldAvatarUrl = user.getAvatarUrl();
                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                    File oldFile = new File(oldAvatarUrl.replace("/uploads/", "uploads/"));
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                // 更新头像URL
                user.setAvatarUrl("/uploads/avatars/" + fileName);
                boolean success = userService.updateUser(user);

                if (success) {
                    Map<String, String> data = new HashMap<>();
                    data.put("avatarUrl", user.getAvatarUrl());
                    return Result.success("头像上传成功", data);
                } else {
                    return Result.badRequest("头像更新失败");
                }
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
