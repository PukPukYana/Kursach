package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserOauth2CredentialId implements Serializable {

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private String externalId;

    @Enumerated(EnumType.STRING)
    private AuthServer authServer;
}
