package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN FETCH p.author LEFT JOIN FETCH p.replies r LEFT JOIN FETCH r.author")
    List<Post> findAllWithAuthorAndReplies();
}