package com.github.ynovice.sbermanager.backend.controller;

import com.github.ynovice.sbermanager.backend.model.CsrfSecurityData;
import com.github.ynovice.sbermanager.backend.service.CsrfTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csrf")
@RequiredArgsConstructor
public class CsrfController {

    private final CsrfTokenRepository csrfTokenRepository;

    @GetMapping
    public ResponseEntity<CsrfSecurityData> getCsrfToken(HttpServletRequest request) {
        return ResponseEntity.ok(csrfTokenRepository.getToken(request));
    }
}
