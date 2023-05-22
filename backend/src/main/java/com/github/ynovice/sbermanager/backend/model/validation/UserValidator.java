package com.github.ynovice.sbermanager.backend.model.validation;

import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.backend.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {

        User user = (User) target;

        validateUsername(user.getUsername(), errors);
        validateEmail(user.getEmail(), errors);
    }

    public void validateUsername(String username, @NonNull Errors errors) {

        if(!StringUtils.hasText(username)) {
            errors.rejectValue(
                    "username",
                    "user.username.blank",
                    "Не может быть пустым"
            );
            return;
        }

        if(username.length() < 4 || username.length() > 16) {
            errors.rejectValue(
                    "username",
                    "user.username.invalidLength",
                    "Должно иметь длину от 4 до 18 символов");
            return;
        }

        for(char c : username.toCharArray()) {
            if((c < 65 || c > 90) && (c < 97 || c > 122)) {  // if not english letter
                errors.rejectValue(
                        "username",
                        "user.username.notOnlyEnglish",
                        "Должно состоять только из латинских букв");
                return;
            }
        }

        if(userRepository.existsByUsername(username)) {
            errors.rejectValue(
                    "username",
                    "user.username.taken",
                    "Уже занято"
            );
        }
    }

    public void validateEmail(String email, @NonNull Errors errors) {

        if(!StringUtils.hasText(email)) {
            errors.rejectValue(
                    "email",
                    "user.email.blank",
                    "Не может быть пустым");
            return;
        }

        if(email.length() > 321) {
            errors.rejectValue(
                    "email",
                    "user.email.tooLong",
                    "Не может быть таким длинным");
        }

        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue(
                    "email",
                    "user.email.invalid",
                    "Невалидный email");
        }

        if(userRepository.existsByEmail(email)) {
            errors.rejectValue(
                    "email",
                    "user.email.taken",
                    "Уже занят");
        }
    }
}
