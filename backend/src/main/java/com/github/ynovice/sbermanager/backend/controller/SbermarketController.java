package com.github.ynovice.sbermanager.backend.controller;

import com.github.ynovice.sbermanager.backend.model.dto.request.ConfirmPhoneNumberRequestDto;
import com.github.ynovice.sbermanager.backend.model.dto.request.SendPhoneConfirmationCodeRequestDto;
import com.github.ynovice.sbermanager.backend.service.SmIntegrationService;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.SendConfirmationCodeResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sm")
@RequiredArgsConstructor
public class SbermarketController {

    private final SmIntegrationService smIntegrationService;

    @Secured("ROLE_USER")
    @PostMapping("/send_code")
    public ResponseEntity<SendConfirmationCodeResponseBody> sendPhoneConfirmationCode(
            @RequestBody SendPhoneConfirmationCodeRequestDto requestDto,
            @AuthenticationPrincipal OAuth2User principal) {

        return ResponseEntity.ok(smIntegrationService.sendPhoneConfirmationCode(requestDto.getEncryptedPhone(),
                principal));
    }

    @Secured("ROLE_USER")
    @PostMapping("/auth")
    public ResponseEntity<Void> confirmPhoneNumber(@RequestBody ConfirmPhoneNumberRequestDto requestDto,
                                                   @AuthenticationPrincipal OAuth2User principal) {

        smIntegrationService.confirmPhoneNumber(requestDto.getRawPhone(),
                requestDto.getEncryptedPhone(),
                requestDto.getConfirmationCode(),
                principal);

        return ResponseEntity.noContent().build();
    }
}
