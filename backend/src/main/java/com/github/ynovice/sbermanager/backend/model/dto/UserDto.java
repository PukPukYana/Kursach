package com.github.ynovice.sbermanager.backend.model.dto;

import com.github.ynovice.sbermanager.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Boolean smLinked;

    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().toString(),
                Objects.nonNull(user.getSmAuthData()));
    }
}
