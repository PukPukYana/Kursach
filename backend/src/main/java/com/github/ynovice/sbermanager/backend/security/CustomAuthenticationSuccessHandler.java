package com.github.ynovice.sbermanager.backend.security;

import com.github.ynovice.sbermanager.backend.model.Role;
import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Value("${app.oauth.successRedirectUrl}")
    private String successRedirectUrl;

    @Value("${app.oauth.successRedirectUrlGuests}")
    private String successRedirectUrlGuests;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        User user = userService.getUserByPrincipal((OAuth2User) authentication.getPrincipal());

        if(user.getRole().equals(Role.GUEST)) {
            response.sendRedirect(successRedirectUrlGuests);
        } else {
            response.sendRedirect(successRedirectUrl);
        }
    }
}
