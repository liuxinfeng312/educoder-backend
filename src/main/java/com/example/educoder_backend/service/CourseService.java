package com.example.educoder_backend.service;

import com.example.educoder_backend.entity.Course;
import com.example.educoder_backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }
}