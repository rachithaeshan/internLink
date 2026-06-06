package com.internlink.internlink.service;

import com.internlink.internlink.entity.Student;
import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.StudentRepository;
import com.internlink.internlink.repository.UserRepository;
import com.internlink.internlink.security.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        OAuth2UserInfo userInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

        String email = userInfo.getEmail();
        String name = userInfo.getName();

        if (email == null) throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");

        // Create user if not exists
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPassword("OAUTH2_USER"); // placeholder — they won't use password login
            user.setRole(User.Role.STUDENT);  // default role
            user.setBlocked(false);
            User savedUser = userRepository.save(user);

            // Create student profile
            Student student = new Student();
            student.setUser(savedUser);
            studentRepository.save(student);
        } else {
            // Self-healing: if user exists but lacks Student profile
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null && user.getRole() == User.Role.STUDENT && studentRepository.findByUserId(user.getId()).isEmpty()) {
                Student student = new Student();
                student.setUser(user);
                studentRepository.save(student);
            }
        }

        return oAuth2User;
    }
}