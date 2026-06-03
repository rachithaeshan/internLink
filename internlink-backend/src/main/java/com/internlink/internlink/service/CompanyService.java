package com.internlink.internlink.service;

import com.internlink.internlink.entity.Application;
import com.internlink.internlink.entity.Company;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.ApplicationRepository;
import com.internlink.internlink.repository.CompanyRepository;
import com.internlink.internlink.repository.InternshipRepository;
import com.internlink.internlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    private Company getCompanyByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return companyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public Internship createInternship(String email, Internship internship) {
        Company company = getCompanyByEmail(email);
        internship.setCompany(company);
        // Auto-approve for development/testing
        // TODO: Change back to false for production
        internship.setApproved(true);
        return internshipRepository.save(internship);
    }

    public List<Internship> getMyInternships(String email) {
        Company company = getCompanyByEmail(email);
        return internshipRepository.findByCompanyId(company.getId());
    }

    public Internship updateInternship(String email, Long id, Internship updated) {
        Company company = getCompanyByEmail(email);
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Not authorized");
        }
        internship.setTitle(updated.getTitle());
        internship.setDescription(updated.getDescription());
        internship.setLocation(updated.getLocation());
        internship.setSkills(updated.getSkills());
        internship.setDuration(updated.getDuration());
        internship.setDeadline(updated.getDeadline());
        return internshipRepository.save(internship);
    }

    public void deleteInternship(String email, Long id) {
        Company company = getCompanyByEmail(email);
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Not authorized");
        }
        internshipRepository.delete(internship);
    }

    public List<Application> getApplicants(String email, Long internshipId) {
        Company company = getCompanyByEmail(email);
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));
        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Not authorized");
        }
        return applicationRepository.findByInternshipId(internshipId);
    }

    public Application updateApplicationStatus(String email, Long applicationId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(Application.Status.valueOf(status.toUpperCase()));
        return applicationRepository.save(application);
    }
}