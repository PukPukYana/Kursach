package com.github.ynovice.sbermanager.backend.model.validation;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PhoneValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return clazz.equals(String.class);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {

        String phone = (String) target;

        // if both false
        if((phone.length() == 12 && phone.startsWith("+7")) == (phone.length() == 11 && phone.startsWith("8"))) {
            errors.reject("phone.invalidLength", "Некорректная длина");
            return;
        }

        if(phone.startsWith("+7")) {
            phone = phone.substring(1);
        }

        for(char c : phone.toCharArray()) {
            if(!Character.isDigit(c)) {
                errors.reject("phone.invalidChar", "Должен состоять только из цифр");
            }
        }
    }
}
