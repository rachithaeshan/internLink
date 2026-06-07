package com.internlink.internlink.service;

import com.internlink.internlink.dto.StudentProfileDTO;
import com.internlink.internlink.entity.Application;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.Student;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.ApplicationRepository;
import com.internlink.internlink.repository.InternshipRepository;
import com.internlink.internlink.repository.StudentRepository;
import com.internlink.internlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    private Student getOrCreateStudent(User user) {
        return studentRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Student student = new Student();
                    student.setUser(user);
                    return studentRepository.save(student);
                });
    }

    public Student getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getOrCreateStudent(user);
    }

    public Student updateProfile(String email, StudentProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(dto.getName());
        userRepository.save(user);

        Student student = getOrCreateStudent(user);
        student.setPhone(dto.getPhone());
        student.setUniversity(dto.getUniversity());
        student.setDegree(dto.getDegree());
        student.setSkills(dto.getSkills());
        student.setGithub(dto.getGithub());
        student.setLinkedin(dto.getLinkedin());
        return studentRepository.save(student);
    }

    public List<Internship> searchInternships(String title, String location, String skills) {
        List<Internship> internships;
        
        // For development: show all internships (approved + pending)
        // TODO: Remove this for production - only show approved
        if (title != null && !title.isEmpty()) {
            internships = internshipRepository.findByTitleContainingIgnoreCase(title);
        } else if (location != null && !location.isEmpty()) {
            internships = internshipRepository.findByLocationContainingIgnoreCase(location);
        } else if (skills != null && !skills.isEmpty()) {
            internships = internshipRepository.findBySkillsContainingIgnoreCase(skills);
        } else {
            internships = internshipRepository.findAll();
        }
        
        return internships;
    }

    public Application applyForInternship(String email, Long internshipId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (applicationRepository.existsByStudentIdAndInternshipId(student.getId(), internshipId)) {
            throw new RuntimeException("Already applied for this internship");
        }

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(Application.Status.PENDING);
        Application saved = applicationRepository.save(application);

        // Email to student — application confirmation
        emailService.sendApplicationConfirmation(
                user.getEmail(),
                user.getName(),
                internship.getTitle(),
                internship.getCompany().getCompanyName()
        );

        // Email to company — new application alert
        emailService.sendNewApplicationAlert(
                internship.getCompany().getUser().getEmail(),
                internship.getCompany().getCompanyName(),
                user.getName(),
                internship.getTitle()
        );

        return saved;
    }

    public List<Application> getMyApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = getOrCreateStudent(user);
        return applicationRepository.findByStudentId(student.getId());
    }

    public Student updateResumeUrl(String email, String resumeUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = getOrCreateStudent(user);
        student.setResumeUrl(resumeUrl);
        return studentRepository.save(student);
    }
}