package com.example.educoder_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Long problemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testCaseInput;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testCaseOutput;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonBackReference // 防止循环引用
    private User teacher;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    @JsonManagedReference // 防止循环引用
    private List<Submission> submissions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestCaseInput() {
        return testCaseInput;
    }

    public void setTestCaseInput(String testCaseInput) {
        this.testCaseInput = testCaseInput;
    }

    public String getTestCaseOutput() {
        return testCaseOutput;
    }

    public void setTestCaseOutput(String testCaseOutput) {
        this.testCaseOutput = testCaseOutput;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
}