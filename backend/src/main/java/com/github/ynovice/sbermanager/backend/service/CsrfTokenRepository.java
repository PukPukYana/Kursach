package com.github.ynovice.sbermanager.backend.service;

import com.github.ynovice.sbermanager.backend.model.CsrfSecurityData;
import jakarta.servlet.http.HttpServletRequest;

public interface CsrfTokenRepository {

    CsrfSecurityData getToken(HttpServletRequest request);
}
