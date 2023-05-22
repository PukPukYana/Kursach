package com.github.ynovice.sbermanager.backend.model.validation;

import com.github.ynovice.sbermanager.backend.model.ItemStackShelfLifeOption;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemStackShelfLifeOptionValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ItemStackShelfLifeOption.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {

        ItemStackShelfLifeOption shelfLifeOption = (ItemStackShelfLifeOption) target;

        validateStorageMode(shelfLifeOption, errors);
        validateShelfLifePresentation(shelfLifeOption, errors);
    }

    public void validateStorageMode(@NonNull ItemStackShelfLifeOption itemStackShelfLifeOption,
                                    @NonNull Errors errors) {

        String storageMode = itemStackShelfLifeOption.getStorageMode();

        if(storageMode != null && storageMode.length() > 255) {

            if(itemStackShelfLifeOption.isPrimary()) {
                errors.rejectValue(
                        "storageMode",
                        "primaryItemStackShelfLifeOption.storageMode.invalid",
                        "Длина текста условий хранения не должна быть больше 255 символов"
                );
            } else {
                errors.rejectValue(
                        "storageMode",
                        "afterOpeningItemStackShelfLifeOption.storageMode.invalid",
                        "Длина текста условий хранения не должна быть больше 255 символов"
                );
            }
        }
    }

    public void validateShelfLifePresentation(@NonNull ItemStackShelfLifeOption itemStackShelfLifeOption,
                                              @NonNull Errors errors) {

        boolean shelfLifePresentationIsValid = itemStackShelfLifeOption.getShelfLifePresentation() == null
                || itemStackShelfLifeOption.getShelfLifeInSeconds() != null;

        if(!shelfLifePresentationIsValid) {

            if(itemStackShelfLifeOption.isPrimary()) {
                errors.rejectValue(
                        "shelfLifePresentation",
                        "primaryItemStackShelfLifeOption.shelfLifePresentation.invalid",
                        "Невалидный срок годности"
                );
            } else {
                errors.rejectValue(
                        "shelfLifePresentation",
                        "afterOpeningItemStackShelfLifeOption.shelfLifePresentation.invalid",
                        "Невалидный срок годности"
                );
            }
        }
    }
}
