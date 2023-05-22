package com.github.ynovice.sbermanager.backend.repository;

import com.github.ynovice.sbermanager.backend.model.AuthServer;
import com.github.ynovice.sbermanager.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("select case when (count(c) > 0) then true else false end" +
            " from UserOauth2Credential c where c.id.externalId = ?1 and c.id.authServer = ?2")
    Boolean existsByExternalIdAndAuthServer(String externalId, AuthServer authServer);

        @Query("select c.id.user from UserOauth2Credential c where c.id.externalId = ?1 and c.id.authServer = ?2")
        Optional<User> findByExternalIdAndAuthServer(String externalId, AuthServer authServer);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
