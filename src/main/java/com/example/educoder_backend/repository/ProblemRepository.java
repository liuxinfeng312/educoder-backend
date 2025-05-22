package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByTeacherId(Long teacherId);
}