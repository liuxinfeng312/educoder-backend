package com.example.educoder_backend.repository;

import com.example.educoder_backend.entity.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    @Query("SELECT t FROM Thread t JOIN FETCH t.user LEFT JOIN FETCH t.responses r LEFT JOIN FETCH r.user")
    List<Thread> findAllWithUserAndResponses();
}