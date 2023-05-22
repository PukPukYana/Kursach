package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sm_auth_data")
@Getter
@Setter
public class SmAuthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 700)
    private String instamartSession;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String encryptedPhone;

    @Column(nullable = false)
    private Long smUserId;

    @OneToOne(mappedBy = "smAuthData")
    private User user;
}
