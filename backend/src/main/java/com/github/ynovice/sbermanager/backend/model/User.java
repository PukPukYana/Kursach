package com.github.ynovice.sbermanager.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL)
    private Set<UserOauth2Credential> oauth2Credentials;

    private String username;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", unique = true)
    private SmAuthData smAuthData;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<ItemStack> itemStacks;

    public String getPhone() {
        if(smAuthData == null) return null;
        return smAuthData.getPhone();
    }
}
