package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.Grade;
import com.example.educoder_backend.entity.Project;
import com.example.educoder_backend.entity.User;
import com.example.educoder_backend.repository.GradeRepository;
import com.example.educoder_backend.repository.ProjectRepository;
import com.example.educoder_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.hibernate.Hibernate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    // 上传文件存储路径
    private static final String UPLOAD_DIR = "uploads/screenshots/";

    // 获取所有学生作品，并加载评分信息
    @GetMapping
    public List<Project> getAllProjects() {
        logger.debug("收到获取所有学生作品请求");
        List<Project> projects = projectRepository.findAll();
        logger.debug("找到 {} 个项目", projects.size());
        for (Project project : projects) {
            Hibernate.initialize(project.getStudent());
            List<Grade> grades = gradeRepository.findByProjectId(project.getId());
            logger.debug("项目 ID: {} 的评分数量: {}", project.getId(), grades.size());
            project.setGrades(grades);
            for (Grade grade : grades) {
                Hibernate.initialize(grade.getTeacher());
                logger.debug("评分 ID: {}, 分数: {}, 评语: {}, 教师: {}",
                        grade.getId(), grade.getScore(), grade.getComment(),
                        grade.getTeacher() != null ? grade.getTeacher().getUsername() : "未加载");
            }
        }
        return projects;
    }

    // 提交评分
    @PostMapping("/grade")
    public ResponseEntity<?> submitGrade(
            @RequestBody GradeRequest request) {
        logger.debug("收到提交评分请求，项目ID: {}, 分数: {}, 评语: {}, 教师ID: {}",
                request.getProjectId(), request.getScore(), request.getComment(), request.getTeacherId());

        try {
            Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
            if (!projectOpt.isPresent()) {
                logger.warn("项目未找到，ID: {}", request.getProjectId());
                return ResponseEntity.status(404).body("项目未找到");
            }

            Optional<User> teacherOpt = userRepository.findById(request.getTeacherId());
            if (!teacherOpt.isPresent()) {
                logger.warn("教师未找到，ID: {}", request.getTeacherId());
                return ResponseEntity.status(404).body("教师未找到");
            }

            Grade grade = new Grade();
            grade.setProject(projectOpt.get());
            grade.setScore(request.getScore());
            grade.setComment(request.getComment());
            grade.setTeacher(teacherOpt.get());
            grade.setCreatedAt(LocalDateTime.now());
            grade.setUpdatedAt(LocalDateTime.now());

            Grade savedGrade = gradeRepository.save(grade);
            logger.debug("评分保存成功，ID: {}", savedGrade.getId());
            return ResponseEntity.ok("评分提交成功");
        } catch (Exception e) {
            logger.error("提交评分失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("提交评分失败：" + e.getMessage());
        }
    }

    // 学生上传作品
    @PostMapping
    public ResponseEntity<?> uploadProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("codeLink") String codeLink,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot,
            @RequestParam("studentId") Long studentId) {
        logger.debug("收到上传作品请求，标题: {}, 学生ID: {}", title, studentId);

        try {
            Optional<User> studentOpt = userRepository.findById(studentId);
            if (!studentOpt.isPresent()) {
                logger.warn("学生未找到，ID: {}", studentId);
                return ResponseEntity.status(404).body("学生未找到");
            }

            Project project = new Project();
            project.setTitle(title);
            project.setDescription(description);
            project.setCodeLink(codeLink);
            project.setStudent(studentOpt.get());
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());

            // 处理截图文件上传
            if (screenshot != null && !screenshot.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + screenshot.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    logger.info("创建上传目录: {}", uploadPath);
                }
                File file = new File(uploadPath.toString(), fileName);
                screenshot.transferTo(file);
                project.setScreenshot("/" + UPLOAD_DIR + fileName);
                logger.debug("截图文件保存成功: {}", file.getAbsolutePath());
            }

            Project savedProject = projectRepository.save(project);
            logger.debug("作品上传成功，ID: {}", savedProject.getId());
            return ResponseEntity.ok(savedProject);
        } catch (IOException e) {
            logger.error("文件上传失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("上传作品失败，错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("上传作品失败：" + e.getMessage());
        }
    }
}

class GradeRequest {
    private Long projectId;
    private int score;
    private String comment;
    private Long teacherId;

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}