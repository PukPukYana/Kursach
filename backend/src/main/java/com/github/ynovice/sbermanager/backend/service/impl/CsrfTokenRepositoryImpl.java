package com.github.ynovice.sbermanager.backend.service.impl;

import com.github.ynovice.sbermanager.backend.model.CsrfSecurityData;
import com.github.ynovice.sbermanager.backend.service.CsrfTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
//шифровка токен
@Service
public class CsrfTokenRepositoryImpl implements CsrfTokenRepository {

    @Override
    public CsrfSecurityData getToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return new CsrfSecurityData(csrfToken.getToken(), csrfToken.getHeaderName());
    }
}
