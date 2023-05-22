package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_oauth2_credentials")
@Getter
@Setter
public class UserOauth2Credential {

    @EmbeddedId
    private UserOauth2CredentialId id;
}
