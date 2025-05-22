package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByAuthorId(Long authorId);
}