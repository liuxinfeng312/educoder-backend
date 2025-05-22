package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByProblemProblemId(Long problemId); // 修改方法名
    List<Submission> findByAssignmentId(Long assignmentId);
}