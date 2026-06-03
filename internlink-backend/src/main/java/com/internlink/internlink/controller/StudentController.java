package com.internlink.internlink.controller;

import com.internlink.internlink.dto.StudentProfileDTO;
import com.internlink.internlink.entity.Application;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.Student;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.UserRepository;
import com.internlink.internlink.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<Student> getProfile(Authentication auth) {
        return ResponseEntity.ok(studentService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<Student> updateProfile(Authentication auth,
                                                 @RequestBody StudentProfileDTO dto) {
        return ResponseEntity.ok(studentService.updateProfile(auth.getName(), dto));
    }

    @PostMapping("/upload-resume")
    public ResponseEntity<String> uploadResume(Authentication auth,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/resumes/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.write(path, file.getBytes());
        studentService.updateResumeUrl(auth.getName(), path.toString());
        return ResponseEntity.ok("Resume uploaded: " + filename);
    }

    @GetMapping("/internships/search")
    public ResponseEntity<List<Internship>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills) {
        return ResponseEntity.ok(studentService.searchInternships(title, location, skills));
    }

    @PostMapping("/apply/{internshipId}")
    public ResponseEntity<Application> apply(Authentication auth,
                                             @PathVariable Long internshipId) {
        return ResponseEntity.ok(studentService.applyForInternship(auth.getName(), internshipId));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> myApplications(Authentication auth) {
        return ResponseEntity.ok(studentService.getMyApplications(auth.getName()));
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(Authentication auth,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/profiles/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.write(path, file.getBytes());

        // Save to user
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePicture(path.toString());
        userRepository.save(user);

        return ResponseEntity.ok("uploads/profiles/" + filename);
    }
}