package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCourseId(Long courseId);
}