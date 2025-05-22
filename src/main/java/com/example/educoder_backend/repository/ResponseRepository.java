package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByThreadId(Long threadId);
}