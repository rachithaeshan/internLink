package com.internlink.internlink.controller;

import com.internlink.internlink.entity.Application;
import com.internlink.internlink.entity.Internship;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.UserRepository;
import com.internlink.internlink.service.CompanyService;
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
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final UserRepository userRepository;

    @PostMapping("/internships")
    public ResponseEntity<Internship> create(Authentication auth,
                                             @RequestBody Internship internship) {
        return ResponseEntity.ok(companyService.createInternship(auth.getName(), internship));
    }

    @GetMapping("/internships")
    public ResponseEntity<List<Internship>> myInternships(Authentication auth) {
        return ResponseEntity.ok(companyService.getMyInternships(auth.getName()));
    }

    @PutMapping("/internships/{id}")
    public ResponseEntity<Internship> update(Authentication auth,
                                             @PathVariable Long id,
                                             @RequestBody Internship internship) {
        return ResponseEntity.ok(companyService.updateInternship(auth.getName(), id, internship));
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        companyService.deleteInternship(auth.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internships/{id}/applicants")
    public ResponseEntity<List<Application>> applicants(Authentication auth,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(companyService.getApplicants(auth.getName(), id));
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<Application> updateStatus(Authentication auth,
                                                    @PathVariable Long id,
                                                    @RequestParam String status) {
        return ResponseEntity.ok(companyService.updateApplicationStatus(auth.getName(), id, status));
    }
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(Authentication auth,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/profiles/";
        Files.createDirectories(Paths.get(uploadDir));
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.write(path, file.getBytes());

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePicture(path.toString());
        userRepository.save(user);

        return ResponseEntity.ok("uploads/profiles/" + filename);
    }
}