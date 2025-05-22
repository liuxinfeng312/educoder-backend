package com.example.educoder_backend.controller;

import com.example.educoder_backend.entity.Problem;
import com.example.educoder_backend.entity.Submission;
import com.example.educoder_backend.entity.User;
import com.example.educoder_backend.repository.ProblemRepository;
import com.example.educoder_backend.repository.SubmissionRepository;
import com.example.educoder_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.hibernate.Hibernate;

@RestController
@RequestMapping("/api")
public class ProblemController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    // 获取所有题目
    @GetMapping("/problems")
    public ResponseEntity<?> getProblems(@RequestParam(value = "category", required = false) String category) {
        logger.debug("收到获取题目请求，分类: {}", category);
        try {
            List<Problem> problems = problemRepository.findAll();
            // 手动加载关联字段
            for (Problem problem : problems) {
                Hibernate.initialize(problem.getTeacher());
                Hibernate.initialize(problem.getSubmissions());
            }
            return ResponseEntity.ok(problems);
        } catch (Exception e) {
            logger.error("获取题目列表失败，错误: {}", e.getMessage());
            return ResponseEntity.status(500).body("获取题目列表失败：" + e.getMessage());
        }
    }

    // 根据 problemId 获取题目
    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getProblemById(@PathVariable Long problemId) {
        logger.debug("收到获取题目请求，题目ID: {}", problemId);
        try {
            Problem problem = problemRepository.findById(problemId)
                    .orElseThrow(() -> new RuntimeException("题目未找到"));
            Hibernate.initialize(problem.getTeacher());
            Hibernate.initialize(problem.getSubmissions());
            return ResponseEntity.ok(problem);
        } catch (Exception e) {
            logger.error("获取题目失败，题目ID: {}, 错误: {}", problemId, e.getMessage());
            return ResponseEntity.status(404).body("题目未找到：" + e.getMessage());
        }
    }

    // 教师发布题目
    @PostMapping("/problems")
    public ResponseEntity<?> createProblem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("testCaseInput") String testCaseInput,
            @RequestParam("testCaseOutput") String testCaseOutput,
            @RequestParam("teacherId") Long teacherId) {
        logger.debug("收到发布题目请求，教师ID: {}", teacherId);
        try {
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("教师未找到"));
            if (!User.Role.TEACHER.equals(teacher.getRole())) {
                throw new RuntimeException("只有教师可以发布题目");
            }

            Problem problem = new Problem();
            problem.setTitle(title);
            problem.setDescription(description);
            problem.setTestCaseInput(testCaseInput);
            problem.setTestCaseOutput(testCaseOutput);
            problem.setTeacher(teacher);
            problem.setCreatedAt(LocalDateTime.now());

            Problem savedProblem = problemRepository.save(problem);
            logger.debug("题目发布成功，题目ID: {}", savedProblem.getProblemId());
            return ResponseEntity.ok(savedProblem);
        } catch (Exception e) {
            logger.error("发布题目失败，教师ID: {}, 错误: {}", teacherId, e.getMessage());
            return ResponseEntity.status(400).body("发布题目失败：" + e.getMessage());
        }
    }

    // 学生提交代码
    @PostMapping("/submissions")
    public ResponseEntity<?> submitCode(
            @RequestParam("studentId") Long studentId,
            @RequestParam("problemId") Long problemId,
            @RequestParam("code") String code) {
        logger.debug("收到代码提交请求，学生ID: {}, 题目ID: {}", studentId, problemId);
        try {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生未找到，ID：" + studentId));
            if (!User.Role.STUDENT.equals(student.getRole())) {
                throw new RuntimeException("只有学生可以提交代码");
            }
            Problem problem = problemRepository.findById(problemId)
                    .orElseThrow(() -> new RuntimeException("题目未找到，ID：" + problemId));

            String result = code.contains("twoSum") ? "通过" : "失败";

            Submission submission = new Submission();
            submission.setStudent(student);
            submission.setProblem(problem);
            submission.setContent(code);
            submission.setResult(result);
            submission.setSubmittedAt(LocalDateTime.now());

            Submission savedSubmission = submissionRepository.save(submission);
            logger.debug("代码提交成功，提交ID: {}", savedSubmission.getId());
            return ResponseEntity.ok(savedSubmission);
        } catch (Exception e) {
            logger.error("代码提交失败，学生ID: {}, 题目ID: {}, 错误: {}", studentId, problemId, e.getMessage());
            return ResponseEntity.status(500).body("代码提交失败：" + e.getMessage());
        }
    }

    // 获取学生的提交记录
    @GetMapping("/submissions/student/{studentId}")
    public ResponseEntity<?> getSubmissionsByStudent(@PathVariable Long studentId) {
        logger.debug("收到获取学生提交记录请求，学生ID: {}", studentId);
        try {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            logger.error("获取学生提交记录失败，学生ID: {}, 错误: {}", studentId, e.getMessage());
            return ResponseEntity.status(500).body("获取学生提交记录失败：" + e.getMessage());
        }
    }

    // 获取题目的提交记录
    @GetMapping("/submissions/problem/{problemId}")
    public ResponseEntity<?> getSubmissionsByProblem(@PathVariable Long problemId) {
        logger.debug("收到获取题目提交记录请求，题目ID: {}", problemId);
        try {
            List<Submission> submissions = submissionRepository.findByProblemProblemId(problemId);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            logger.error("获取题目提交记录失败，题目ID: {}, 错误: {}", problemId, e.getMessage());
            return ResponseEntity.status(500).body("获取题目提交记录失败：" + e.getMessage());
        }
    }

    // 获取作业的提交记录
    @GetMapping("/submissions/assignment/{assignmentId}")
    public ResponseEntity<?> getSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        logger.debug("收到获取作业提交记录请求，作业ID: {}", assignmentId);
        try {
            List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            logger.error("获取作业提交记录失败，作业ID: {}, 错误: {}", assignmentId, e.getMessage());
            return ResponseEntity.status(500).body("获取作业提交记录失败：" + e.getMessage());
        }
    }

    // 模拟提交（旧方法，保留但不使用）
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitCodeLegacy(
            @RequestBody Map<String, Object> submission) {
        logger.debug("收到代码提交请求: {}", submission);
        String language = (String) submission.get("language");
        String code = (String) submission.get("code");
        Integer problemId = (Integer) submission.get("problemId");

        Map<String, Object> result = new HashMap<>();
        if (code.contains("twoSum")) {
            result.put("status", "通过");
            result.put("output", "[0, 1]");
            result.put("message", "答案正确！");
        } else {
            result.put("status", "失败");
            result.put("message", "答案错误，请检查你的代码！");
        }
        return ResponseEntity.ok(result);
    }
}