package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostId(Long postId);
}