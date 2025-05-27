package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.student.id = :studentId")
    List<Project> findByStudentId(@Param("studentId") Long studentId);
}