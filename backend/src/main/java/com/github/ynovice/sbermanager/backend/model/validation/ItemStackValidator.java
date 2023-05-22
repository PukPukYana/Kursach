package com.github.ynovice.sbermanager.backend.model.validation;

import com.github.ynovice.sbermanager.backend.model.ItemStack;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.net.URL;

@Component
public class ItemStackValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ItemStack.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {

        ItemStack itemStack = (ItemStack) target;

        validateName(itemStack, errors);
        validateDescription(itemStack, errors);
        validateAmountPerPack(itemStack, errors);
        validateCount(itemStack, errors);
        validatePlacedAt(itemStack, errors);
        validateImageUrl(itemStack, errors);
    }

    public void validateName(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        String name = itemStack.getName();

        if(!StringUtils.hasText(name)) {
            errors.rejectValue(
                    "name",
                    "itemStack.name.empty",
                    "Имя товара не может быть пустым"
            );
            return;
        }

        if(name.length() > 255) {
            errors.rejectValue(
                    "name",
                    "itemStack.name.empty",
                    "Имя товара не должно быть длиньше 255 символов"
            );
        }
    }

    public void validateDescription(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        String description = itemStack.getDescription();

        if(description == null) {
            return;
        }

        if(description.length() > 2000) {
            errors.rejectValue(
                    "description",
                    "itemStack.description.tooLong",
                    "Описание не должно быть длиньше 2000 симвлолов"
            );
        }
    }

    public void validateAmountPerPack(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        String amountPerPack = itemStack.getAmountPerPack();

        if(amountPerPack != null && amountPerPack.length() > 10) {
            errors.rejectValue(
                    "amountPerPack",
                    "itemStack.amountPerPack.tooLong",
                    "Длина не должна превышать 10 символов"
            );
        }
    }

    public void validateCount(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        Integer count = itemStack.getCount();

        if(count == null) {
            errors.rejectValue(
                    "count",
                    "itemStack.count.empty",
                    "Количество не указано или указано неверно"
            );
            return;
        }

        if(count <= 0) {
            errors.rejectValue(
                    "count",
                    "itemStack.count.negative",
                    "Количество не может быть меньше нуля"
            );
        }
    }

    public void validatePlacedAt(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        if(itemStack.getPlacedAt() == null) {
            errors.rejectValue(
                    "placedAt",
                    "itemStack.placedAt.empty",
                    "Дата производства не указана или указана неверно"
            );
        }
    }

    public void validateImageUrl(@NonNull ItemStack itemStack, @NonNull Errors errors) {

        if(itemStack.getImageUrl() == null) return;

        try {
            new URL(itemStack.getImageUrl()).toURI();
        } catch (Exception e) {
            errors.rejectValue(
                    "imageUrl",
                    "itemStack.imageUrl.invalid",
                    "Невалидная ссылка"
            );
        }
    }
}
