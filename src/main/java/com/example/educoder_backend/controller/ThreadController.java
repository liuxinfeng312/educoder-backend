package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.*;
import com.example.educoder_backend.entity.Thread;
import com.example.educoder_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);

    @Autowired
    private ThreadRepository threadRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取所有帖子
    @GetMapping
    public ResponseEntity<?> getAllThreads() {
        logger.debug("收到获取所有帖子请求");
        try {
            List<Thread> threads = threadRepository.findAllWithUserAndResponses();
            logger.debug("查询到 {} 条帖子", threads.size());
            return ResponseEntity.ok(threads);
        } catch (Exception e) {
            logger.error("获取帖子列表失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取帖子列表失败：" + e.getMessage());
        }
    }

    // 创建帖子
    @PostMapping
    public ResponseEntity<?> createThread(@RequestBody ThreadRequest request) {
        logger.debug("收到创建帖子请求，作者ID: {}", request.getUserId());
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("用户未找到"));
            Thread thread = new Thread();
            thread.setTitle(request.getTitle());
            thread.setContent(request.getContent());
            thread.setCategory(request.getCategory());
            thread.setUser(user);
            thread.setCreatedAt(LocalDateTime.now());
            thread.setUpdatedAt(LocalDateTime.now());
            Thread savedThread = threadRepository.save(thread);
            return ResponseEntity.ok(savedThread);
        } catch (Exception e) {
            logger.error("创建帖子失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("创建帖子失败：" + e.getMessage());
        }
    }

    // 获取帖子详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getThreadById(@PathVariable Long id) {
        logger.debug("收到获取帖子详情请求，帖子ID: {}", id);
        try {
            Optional<Thread> thread = threadRepository.findById(id);
            if (thread.isPresent()) {
                thread.get().setViews(thread.get().getViews() + 1);
                Thread updatedThread = threadRepository.save(thread.get());
                return ResponseEntity.ok(updatedThread);
            }
            logger.warn("帖子未找到，ID: {}", id);
            return ResponseEntity.status(404).body("帖子未找到");
        } catch (Exception e) {
            logger.error("获取帖子失败，帖子ID: {}, 错误: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取帖子失败：" + e.getMessage());
        }
    }

    // 获取帖子回复
    @GetMapping("/{threadId}/responses")
    public ResponseEntity<?> getResponsesByThreadId(@PathVariable Long threadId) {
        logger.debug("收到获取帖子回复请求，帖子ID: {}", threadId);
        try {
            List<Response> responses = responseRepository.findByThreadId(threadId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("获取回复失败，帖子ID: {}, 错误: {}", threadId, e.getMessage(), e);
            return ResponseEntity.status(500).body("获取回复失败：" + e.getMessage());
        }
    }
}

class ThreadRequest {
    private String title;
    private String content;
    private String category;
    private Long userId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}