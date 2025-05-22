package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Add query method to find courses by category
    List<Course> findByCategory(String category);
}