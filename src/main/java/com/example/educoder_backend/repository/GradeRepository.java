package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    @Query("SELECT g FROM Grade g WHERE g.project.id = :projectId")
    List<Grade> findByProjectId(@Param("projectId") Long projectId);
}