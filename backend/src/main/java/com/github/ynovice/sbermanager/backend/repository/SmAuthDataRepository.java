package com.github.ynovice.sbermanager.backend.repository;

import com.github.ynovice.sbermanager.backend.model.SmAuthData;
import com.github.ynovice.sbermanager.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmAuthDataRepository extends JpaRepository<SmAuthData, Long> {

    void deleteAllByUser(User user);
}
