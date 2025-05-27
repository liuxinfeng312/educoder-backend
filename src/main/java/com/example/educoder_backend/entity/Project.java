package com.example.educoder_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "code_link", nullable = false)
    private String codeLink;

    private String screenshot;

    @Column(nullable = false)
    private int progress = 0; // 新增 progress 字段，默认值为 0

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Grade> grades;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCodeLink() { return codeLink; }
    public void setCodeLink(String codeLink) { this.codeLink = codeLink; }
    public String getScreenshot() { return screenshot; }
    public void setScreenshot(String screenshot) { this.screenshot = screenshot; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}