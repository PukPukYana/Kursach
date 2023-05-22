package com.github.ynovice.sbermanager.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CsrfSecurityData {

    private String csrfToken;
    private String csrfHeaderName;
}
