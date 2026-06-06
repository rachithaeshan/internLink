package com.internlink.internlink.security;

import com.internlink.internlink.entity.User;
import com.internlink.internlink.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2UserInfo userInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

        String email = userInfo.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isBlocked()) {
            response.sendRedirect("http://localhost:3000/login?error=blocked");
            return;
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Redirect to frontend with token
        response.sendRedirect(
                "http://localhost:3000/oauth2/callback?token=" + token +
                        "&role=" + user.getRole().name() +
                        "&name=" + java.net.URLEncoder.encode(user.getName(), "UTF-8") +
                        "&userId=" + user.getId()
        );
    }
}