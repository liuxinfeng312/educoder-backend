package com.example.educoder_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("配置 SecurityFilterChain...");
        http
                .csrf(csrf -> {
                    System.out.println("完全禁用 CSRF...");
                    csrf.disable();
                })
                .authorizeHttpRequests(auth -> {
                    System.out.println("配置请求匹配规则...");
                    auth
                            .anyRequest().permitAll(); // 允许所有请求匿名访问
                })
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());
//                .anonymous(anonymous -> anonymous.enable()); // 启用匿名访问

        System.out.println("SecurityFilterChain 配置成功。");
        return http.build();
    }
}