package com.example.android.controller;

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
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.register(user);
            if (success) {
                result.put("code", 200);
                result.put("message", "注册成功");
            }else  {
                result.put("code", 400);
                result.put("message", "注册失败");
            }
        }catch (Exception e){
            result.put("code", 400);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Map<String, Object> loginResult = userService.login(username, password);

            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", loginResult);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getUserById(@PathVariable Long userId) {
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setPasswordHash(null);  // 移除密码等敏感信息
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> updateUser(
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username,
            @RequestParam("studentId") String studentId,
            @RequestParam("school") String school) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setUsername(username);
                user.setStudentId(studentId);
                user.setSchool(school);
                boolean success = userService.updateUser(user);
                if (success) {
                    result.put("code", 200);
                    result.put("message", "更新成功");
                } else {
                    result.put("code", 400);
                    result.put("message", "更新失败");
                }
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/update-security")
    public Map<String, Object> updateAccountSecurity(
            @RequestParam("userId") Long userId,
            @RequestParam("phone") String phone,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setPhone(phone);
                
                if (oldPassword != null && newPassword != null && !oldPassword.isEmpty() && !newPassword.isEmpty()) {
                    boolean passwordChanged = userService.changePassword(userId, oldPassword, newPassword);
                    if (!passwordChanged) {
                        result.put("code", 400);
                        result.put("message", "原密码错误");
                        return result;
                    }
                }
                
                boolean success = userService.updateUser(user);
                if (success) {
                    result.put("code", 200);
                    result.put("message", "更新成功");
                } else {
                    result.put("code", 400);
                    result.put("message", "更新失败");
                }
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, Object> changePasswordData) {
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            Long userId = Long.parseLong(changePasswordData.get("userId").toString());
            String oldPassword = (String) changePasswordData.get("oldPassword");
            String newPassword = (String) changePasswordData.get("newPassword");

            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                result.put("code", 200);
                result.put("message", "密码修改成功");
            } else {
                result.put("code", 400);
                result.put("message", "密码修改失败");
            }
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        }

        return result;
    }
    @PostMapping("/upload-avatar")
    public Map<String,Object> uploadAvatar(
            @RequestParam("file")MultipartFile file,
            @RequestParam("userId")Long userId
            ){
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验证文件是否为空
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "文件为空");
                return result;
            }

            // 2. 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("code", 400);
                result.put("message", "只能上传图片文件");
                return result;
            }

            // 3. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString() + extension;

            // 4. 创建保存目录
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 5. 保存文件
            String filePath = uploadDir + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // 6. 更新用户头像URL
            User user = userService.getUserById(userId);
            if (user != null) {
                // 删除旧头像（可选）
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
                    result.put("code", 200);
                    result.put("message", "头像上传成功");
                    result.put("data", Map.of("avatarUrl", user.getAvatarUrl()));
                } else {
                    result.put("code", 400);
                    result.put("message", "头像更新失败");
                }
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
