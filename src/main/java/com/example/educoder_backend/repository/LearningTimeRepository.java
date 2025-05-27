package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.LearningTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningTimeRepository extends JpaRepository<LearningTime, Long> {
    @Query("SELECT SUM(lt.duration) FROM LearningTime lt WHERE lt.student.id = :studentId")
    Integer getTotalLearningTimeByStudentId(@Param("studentId") Long studentId);

    List<LearningTime> findByStudentId(Long studentId);
}