package com.internlink.internlink.repository;

import com.internlink.internlink.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByInternshipId(Long internshipId);
    boolean existsByStudentIdAndInternshipId(Long studentId, Long internshipId);
}