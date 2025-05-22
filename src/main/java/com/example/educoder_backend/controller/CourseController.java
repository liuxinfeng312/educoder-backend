package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.*;
import com.example.educoder_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取所有课程
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        logger.debug("收到获取所有课程请求");
        List<Course> courses = courseRepository.findAll();
        return ResponseEntity.ok(courses);
    }

    // 获取课程详情
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        logger.debug("收到获取课程详情请求，课程ID: {}", id);
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        }
        logger.warn("课程未找到，ID: {}", id);
        return ResponseEntity.notFound().build();
    }

    // 更新课程信息（教师编辑课程介绍）
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody UpdateCourseRequest request) {
        logger.debug("收到更新课程请求，课程ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程未找到"));
        course.setDescription(request.getDescription());
        course.setUpdatedAt(LocalDateTime.now());
        Course updatedCourse = courseRepository.save(course);
        logger.debug("课程更新成功，ID: {}", id);
        return ResponseEntity.ok(updatedCourse);
    }

    // 按分类获取课程
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        logger.debug("收到按分类获取课程请求，分类: {}", category);
        List<Course> courses = courseRepository.findByCategory(category);
        return ResponseEntity.ok(courses);
    }

    // 获取课程视频
    @GetMapping("/{courseId}/videos")
    public ResponseEntity<List<Video>> getVideosByCourseId(@PathVariable Long courseId) {
        logger.debug("收到获取课程视频请求，课程ID: {}", courseId);
        List<Video> videos = videoRepository.findByCourseId(courseId);
        return ResponseEntity.ok(videos);
    }

    // 教师发布视频
    @PostMapping("/{courseId}/videos")
    public ResponseEntity<Video> createVideo(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("teacherId") Long teacherId) {
        logger.debug("收到发布视频请求，课程ID: {}, 教师ID: {}", courseId, teacherId);
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("教师未找到"));
        if (!teacher.getRole().equals(User.Role.TEACHER)) {
            throw new RuntimeException("只有教师可以发布视频");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程未找到"));

        String baseDir = System.getProperty("user.dir");
        String uploadDir = baseDir + File.separator + "uploads" + File.separator + "videos" + File.separator;
        logger.debug("上传目录: {}", uploadDir);
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean dirsCreated = dir.mkdirs();
            if (!dirsCreated) {
                logger.error("无法创建上传目录: {}", uploadDir);
                throw new RuntimeException("无法创建上传目录: " + uploadDir);
            }
            logger.debug("成功创建上传目录: {}", uploadDir);
        }

        String fileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
        File serverFile = new File(uploadDir + fileName);
        try {
            videoFile.transferTo(serverFile);
            logger.debug("视频文件保存成功，路径: {}", serverFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("视频上传失败: {}", e.getMessage());
            throw new RuntimeException("视频上传失败: " + e.getMessage());
        }

        String videoUrl = "/uploads/videos/" + fileName;
        Video video = new Video();
        video.setCourse(course);
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoUrl(videoUrl);
        video.setCreatedAt(LocalDateTime.now());
        video.setUpdatedAt(LocalDateTime.now());
        Video savedVideo = videoRepository.save(video);
        logger.debug("视频发布成功，视频URL: {}", videoUrl);
        return ResponseEntity.ok(savedVideo);
    }

    // 下载视频
    @GetMapping("/videos/download")
    public ResponseEntity<?> downloadVideo(@RequestParam("videoUrl") String videoUrl) {
        logger.debug("收到下载视频请求，videoUrl: {}", videoUrl);
        try {
            // 获取项目根目录
            String baseDir = System.getProperty("user.dir");
            logger.debug("项目根目录: {}", baseDir);

            // 解析 videoUrl，提取相对路径
            String relativePath = videoUrl.startsWith("/uploads/") ? videoUrl.substring("/uploads/".length()) : videoUrl;
            logger.debug("相对路径: {}", relativePath);

            // 构建绝对路径
            String absolutePath = baseDir + File.separator + "uploads" + File.separator + relativePath;
            logger.debug("尝试加载文件的绝对路径: {}", absolutePath);

            // 检查文件是否存在
            File file = new File(absolutePath);
            if (!file.exists()) {
                logger.warn("文件不存在: {}", absolutePath);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "文件不存在: " + absolutePath);
                return ResponseEntity.status(404).body(errorResponse);
            }

            // 检查文件是否可读
            if (!file.canRead()) {
                logger.warn("文件不可读: {}", absolutePath);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "文件不可读: " + absolutePath);
                return ResponseEntity.status(403).body(errorResponse);
            }

            // 检查文件是否可写（确保程序有权限）
            if (!file.canWrite()) {
                logger.warn("文件不可写: {}", absolutePath);
            }

            // 打印文件信息
            logger.debug("文件存在，大小: {} 字节", file.length());
            logger.debug("文件最后修改时间: {}", new java.util.Date(file.lastModified()));

            // 使用 FileSystemResource 加载文件
            Resource resource = new FileSystemResource(file);
            logger.debug("文件加载成功: {}", absolutePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            logger.error("下载视频时发生错误: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "下载视频时发生错误: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 获取课程作业
    @GetMapping("/{courseId}/assignments")
    public ResponseEntity<List<Assignment>> getAssignmentsByCourseId(@PathVariable Long courseId) {
        logger.debug("收到获取课程作业请求，课程ID: {}", courseId);
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        return ResponseEntity.ok(assignments);
    }

    // 教师发布作业
    @PostMapping("/{courseId}/assignments")
    public ResponseEntity<Assignment> createAssignment(
            @PathVariable Long courseId,
            @RequestBody AssignmentRequest request) {
        logger.debug("收到发布作业请求，课程ID: {}, 教师ID: {}", courseId, request.getTeacherId());
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师未找到"));
        if (!teacher.getRole().equals(User.Role.TEACHER)) {
            throw new RuntimeException("只有教师可以发布作业");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程未找到"));
        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        Assignment savedAssignment = assignmentRepository.save(assignment);
        logger.debug("作业发布成功，作业标题: {}", request.getTitle());
        return ResponseEntity.ok(savedAssignment);
    }

    // 学生提交作业
    @PostMapping("/{courseId}/assignments/{assignmentId}/submissions")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("studentId") Long studentId) {
        logger.debug("收到提交作业请求，课程ID: {}, 作业ID: {}, 学生ID: {}", courseId, assignmentId, studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程未找到"));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("作业未找到"));
        if (!assignment.getCourse().getId().equals(course.getId())) {
            throw new RuntimeException("作业不属于该课程");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生未找到"));
        if (!student.getRole().equals(User.Role.STUDENT)) {
            throw new RuntimeException("只有学生可以提交作业");
        }

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            String baseDir = System.getProperty("user.dir");
            String uploadDir = baseDir + File.separator + "uploads" + File.separator + "submissions" + File.separator;
            logger.debug("上传目录: {}", uploadDir);
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean dirsCreated = dir.mkdirs();
                if (!dirsCreated) {
                    logger.error("无法创建上传目录: {}", uploadDir);
                    throw new RuntimeException("无法创建上传目录: " + uploadDir);
                }
                logger.debug("成功创建上传目录: {}", uploadDir);
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File serverFile = new File(uploadDir + fileName);
            try {
                file.transferTo(serverFile);
                logger.debug("提交文件保存成功: {}", serverFile.getAbsolutePath());
            } catch (IOException e) {
                logger.error("文件上传失败: {}", e.getMessage());
                throw new RuntimeException("文件上传失败: " + e.getMessage());
            }
            fileUrl = "/uploads/submissions/" + fileName;
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setFileUrl(fileUrl);
        submission.setSubmittedAt(LocalDateTime.now());
        Submission savedSubmission = submissionRepository.save(submission);
        logger.debug("作业提交成功，学生ID: {}", studentId);
        return ResponseEntity.ok(savedSubmission);
    }

    // 获取作业提交记录
    @GetMapping("/{courseId}/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<Submission>> getSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        logger.debug("收到获取作业提交记录请求，作业ID: {}", assignmentId);
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return ResponseEntity.ok(submissions);
    }
}

class AssignmentRequest {
    private String title;
    private String description;
    private Long teacherId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
}

class UpdateCourseRequest {
    private String description;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}