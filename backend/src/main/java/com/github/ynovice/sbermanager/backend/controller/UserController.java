package com.github.ynovice.sbermanager.backend.controller;

import com.github.ynovice.sbermanager.backend.exception.InvalidFieldsException;
import com.github.ynovice.sbermanager.backend.exception.InvalidObjectException;
import com.github.ynovice.sbermanager.backend.model.dto.UserDto;
import com.github.ynovice.sbermanager.backend.model.dto.request.CompleteUserRegistrationDto;
import com.github.ynovice.sbermanager.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> user(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(UserDto.fromUser(userService.getUserByPrincipal(principal)));
    }

    @PostMapping
    public ResponseEntity<Void> completeUserRegistration(@RequestBody CompleteUserRegistrationDto requestDto,
                                                         @AuthenticationPrincipal OAuth2User principal) {

        userService.completeUserRegistration(principal, requestDto.getUsername(), requestDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/username/validate")
    public ResponseEntity<Void> validateUsername(@RequestParam String username) {

        List<FieldError> fieldErrors = userService.validateUsername(username);
        if (!fieldErrors.isEmpty()) {
            throw new InvalidObjectException(fieldErrors);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/validate")
    public ResponseEntity<Void> validateEmail(@RequestParam String email) {

        List<FieldError> fieldErrors = userService.validateEmail(email);
        if (!fieldErrors.isEmpty()) {
            throw new InvalidObjectException(fieldErrors);
        }

        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/phone/validate")
    public ResponseEntity<Void> validatePhone(@RequestParam String phone) {

        List<ObjectError> objectErrors = userService.validatePhone(phone);
        if (!objectErrors.isEmpty()) {
            throw new InvalidFieldsException(objectErrors);
        }

        return ResponseEntity.ok().build();
    }
}
