package com.example.educoder_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = true)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    @JsonBackReference // 防止循环引用
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference // 防止循环引用
    private User student;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String result;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}