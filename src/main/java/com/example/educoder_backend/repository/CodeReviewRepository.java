package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findByPostId(Long postId);
}