package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.User;
import com.example.educoder_backend.repository.UserRepository;
import com.example.educoder_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request, HttpServletResponse response) throws IOException {
        System.out.println("收到注册请求: " + request.getUsername() + ", 角色: " + request.getRole());
        try {
            User.Role userRole = User.Role.valueOf(request.getRole().toUpperCase());
            userService.registerUser(request.getUsername(), request.getPassword(), userRole);
            System.out.println("用户注册成功: " + request.getUsername());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"用户注册成功\"}");
        } catch (RuntimeException e) {
            System.out.println("注册失败: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("注册时发生未知错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"服务器内部错误: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        System.out.println("收到登录请求: 用户名=" + request.getUsername() + ", 密码=" + request.getPassword());
        try {
            Optional<User> user = userService.loginUser(request.getUsername(), request.getPassword());
            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.get().getId());
                userData.put("username", user.get().getUsername());
                userData.put("role", user.get().getRole().toString());
                response.put("user", userData);
                System.out.println("登录成功: 用户名=" + user.get().getUsername());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("登录失败: 用户名或密码错误");
                return ResponseEntity.badRequest().body(Map.of("error", "用户名或密码错误"));
            }
        } catch (Exception e) {
            System.err.println("登录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        System.out.println("收到获取用户请求: 用户名=" + username);
        try {
            Optional<User> user = userService.getUserByUsername(username);
            if (user.isPresent()) {
                System.out.println("获取用户成功: 用户名=" + username);
                return ResponseEntity.ok(user.get());
            } else {
                System.out.println("获取用户失败: 用户未找到，用户名=" + username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("获取用户时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // 新增通过 ID 获取用户的端点
    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        System.out.println("收到获取用户请求: ID=" + id);
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                System.out.println("获取用户成功: ID=" + id);
                return ResponseEntity.ok(user.get());
            } else {
                System.out.println("获取用户失败: 用户未找到，ID=" + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("获取用户时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(
            @PathVariable String username,
            @RequestBody UpdateRequest request) {
        System.out.println("收到更新用户请求: 用户名=" + username + ", 请求数据=" + request);
        try {
            User updatedUser = userService.updateUser(
                    username,
                    request.getNickname(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getAvatarUrl());
            System.out.println("更新用户成功: 用户名=" + username);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.err.println("更新用户时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<AvatarResponse> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        System.out.println("收到上传头像请求: 文件名=" + file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                System.out.println("上传头像失败: 文件为空");
                return ResponseEntity.badRequest().body(new AvatarResponse("未上传文件"));
            }
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File serverFile = new File(uploadDir + fileName);
            file.transferTo(serverFile);

            String avatarUrl = "/avatars/" + fileName;
            System.out.println("上传头像成功: 头像URL=" + avatarUrl);
            return ResponseEntity.ok(new AvatarResponse(avatarUrl));
        } catch (IOException e) {
            System.err.println("上传头像失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new AvatarResponse("头像上传失败: " + e.getMessage()));
        }
    }

    @Autowired
    private UserRepository userRepository;  // 确保注入 UserRepository
}

class RegisterRequest {
    private String username;
    private String password;
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class UpdateRequest {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String avatarUrl;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}

class AvatarResponse {
    private String avatarUrl;

    public AvatarResponse(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}