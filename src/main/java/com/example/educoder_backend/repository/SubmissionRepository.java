package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.example.educoder_backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudentId(Long studentId);
    List<Submission> findByProblemProblemId(Long problemId); // 修改方法名
    List<Submission> findByAssignmentId(Long assignmentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
}