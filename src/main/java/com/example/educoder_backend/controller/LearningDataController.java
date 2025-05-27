package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.LearningTime;
import com.example.educoder_backend.entity.Project;
import com.example.educoder_backend.entity.Submission;
import com.example.educoder_backend.entity.User;
import com.example.educoder_backend.repository.LearningTimeRepository;
import com.example.educoder_backend.repository.ProjectRepository;
import com.example.educoder_backend.repository.SubmissionRepository;
import com.example.educoder_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/learning-data")
public class LearningDataController {

    private static final Logger logger = LoggerFactory.getLogger(LearningDataController.class);

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LearningTimeRepository learningTimeRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取作业完成率
    @GetMapping("/assignments/completion/{studentId}")
    public ResponseEntity<?> getAssignmentCompletion(@PathVariable Long studentId) {
        logger.debug("收到获取作业完成率请求，学生ID: {}", studentId);
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);
            long completedAssignments = submissions.stream()
                    .filter(s -> "SUBMITTED".equals(s.getResult()) || "GRADED".equals(s.getResult()))
                    .count();
            long totalAssignments = submissionRepository.countByStudentId(studentId);

            Map<String, Long> result = new HashMap<>();
            result.put("completedAssignments", completedAssignments);
            result.put("totalAssignments", totalAssignments);
            logger.debug("作业完成率数据: 已完成 {}, 总数 {}", completedAssignments, totalAssignments);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取作业完成率失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取作业完成率失败：" + e.getMessage());
        }
    }

    // 获取项目进度
    @GetMapping("/projects/progress/{studentId}")
    public ResponseEntity<?> getProjectsProgress(@PathVariable Long studentId) {
        logger.debug("收到获取项目进度请求，学生ID: {}", studentId);
        try {
            List<Project> projects = projectRepository.findByStudentId(studentId);
            logger.debug("找到 {} 个项目", projects.size());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            logger.error("获取项目进度失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取项目进度失败：" + e.getMessage());
        }
    }

    // 获取学习时长
    @GetMapping("/time/{studentId}")
    public ResponseEntity<?> getLearningTime(@PathVariable Long studentId) {
        logger.debug("收到获取学习时长请求，学生ID: {}", studentId);
        try {
            Integer totalLearningTime = learningTimeRepository.getTotalLearningTimeByStudentId(studentId);
            if (totalLearningTime == null) {
                totalLearningTime = 0;
            }
            Map<String, Integer> result = new HashMap<>();
            result.put("totalLearningTime", totalLearningTime);
            logger.debug("累计学习时长: {} 秒", totalLearningTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取学习时长失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("获取学习时长失败：" + e.getMessage());
        }
    }

    // 记录学习时长
    @PostMapping("/time")
    public ResponseEntity<?> recordLearningTime(@RequestBody LearningTimeRequest request) {
        logger.debug("收到记录学习时长请求，学生ID: {}, 时长: {} 秒", request.getStudentId(), request.getDuration());
        try {
            Optional<User> studentOpt = userRepository.findById(request.getStudentId());
            if (!studentOpt.isPresent()) {
                logger.warn("学生未找到，ID: {}", request.getStudentId());
                return ResponseEntity.status(404).body("学生未找到");
            }

            LearningTime learningTime = new LearningTime();
            learningTime.setStudent(studentOpt.get());
            learningTime.setDuration(request.getDuration());
            learningTime.setRecordedAt(LocalDateTime.now());

            LearningTime savedLearningTime = learningTimeRepository.save(learningTime);
            logger.debug("学习时长记录成功，ID: {}", savedLearningTime.getId());
            return ResponseEntity.ok("学习时长记录成功");
        } catch (Exception e) {
            logger.error("记录学习时长失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("记录学习时长失败：" + e.getMessage());
        }
    }
}

class LearningTimeRequest {
    private Long studentId;
    private int duration;

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}