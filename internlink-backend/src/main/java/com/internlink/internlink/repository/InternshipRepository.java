package com.internlink.internlink.repository;

import com.internlink.internlink.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByApprovedTrue();
    List<Internship> findByApprovedFalse();
    List<Internship> findByCompanyId(Long companyId);
    List<Internship> findByApprovedTrueAndTitleContainingIgnoreCase(String title);
    List<Internship> findByApprovedTrueAndLocationContainingIgnoreCase(String location);
    List<Internship> findByApprovedTrueAndSkillsContainingIgnoreCase(String skills);
    
    // For development/testing - show all internships regardless of approval status
    List<Internship> findByTitleContainingIgnoreCase(String title);
    List<Internship> findByLocationContainingIgnoreCase(String location);
    List<Internship> findBySkillsContainingIgnoreCase(String skills);
}