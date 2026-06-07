package com.internlink.internlink.service;

import com.internlink.internlink.dto.AuthResponse;
import com.internlink.internlink.dto.LoginRequest;
import com.internlink.internlink.dto.RegisterRequest;
import com.internlink.internlink.entity.Company;
import com.internlink.internlink.entity.Student;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.CompanyRepository;
import com.internlink.internlink.repository.StudentRepository;
import com.internlink.internlink.repository.UserRepository;
import com.internlink.internlink.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == User.Role.STUDENT) {
            Student student = new Student();
            student.setUser(savedUser);
            student.setUniversity(request.getUniversity());
            student.setDegree(request.getDegree());
            studentRepository.save(student);

            // Send student welcome email
            emailService.sendStudentWelcome(savedUser.getEmail(), savedUser.getName());
        }

        if (savedUser.getRole() == User.Role.COMPANY) {
            Company company = new Company();
            company.setUser(savedUser);
            company.setCompanyName(request.getCompanyName());
            company.setIndustry(request.getIndustry());
            company.setWebsite(request.getWebsite());
            companyRepository.save(company);

            // Send company welcome email
            emailService.sendCompanyWelcome(savedUser.getEmail(), request.getCompanyName());
        }

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return new AuthResponse(token, savedUser.getRole().name(), savedUser.getName(), savedUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name(), user.getName(), user.getId());
    }
}