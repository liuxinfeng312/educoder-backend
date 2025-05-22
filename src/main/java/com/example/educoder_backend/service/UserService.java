package com.example.educoder_backend.service;

import com.example.educoder_backend.entity.User;
import com.example.educoder_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String password, User.Role role) {
        System.out.println("注册用户: 用户名=" + username + ", 角色=" + role);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        System.out.println("用户注册成功: 用户名=" + username + ", 加密后的密码=" + user.getPassword());
        return savedUser;
    }

    public Optional<User> loginUser(String username, String password) {
        System.out.println("尝试登录: 用户名=" + username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            System.out.println("密码验证成功: 用户名=" + username);
            return user;
        }
        System.out.println("登录失败: 用户名=" + username);
        return Optional.empty();
    }

    public Optional<User> getUserByUsername(String username) {
        System.out.println("查找用户: 用户名=" + username);
        return userRepository.findByUsername(username);
    }

    public User updateUser(String username, String nickname, String email, String password, String avatarUrl) {
        System.out.println("更新用户: 用户名=" + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        user.setNickname(nickname);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
            System.out.println("更新密码: 用户名=" + username + ", 新加密密码=" + user.getPassword());
        }
        return userRepository.save(user);
    }
}