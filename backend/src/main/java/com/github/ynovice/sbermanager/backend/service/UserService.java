package com.github.ynovice.sbermanager.backend.service;

import com.github.ynovice.sbermanager.backend.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

public interface UserService {

    User getUserByPrincipal(OAuth2User oAuth2User);

    User registerIfNotRegistered(OAuth2User oAuth2User);

    void completeUserRegistration(OAuth2User oAuth2User, String username, String email);

    List<FieldError> validateUsername(String username);

    List<FieldError> validateEmail(String email);

    List<ObjectError> validatePhone(String phone);

    void save(User user);
}
