package com.github.ynovice.sbermanager.backend.model.dto;

import com.github.ynovice.sbermanager.backend.model.ItemStackShelfLifeOption;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ItemStackShelfLifeOptionDto {

    private Long id;
    private String storageMode;
    private String shelfLifePresentation;
    private ZonedDateTime startCountingFrom;
    private boolean active;
    private ZonedDateTime itemStackGoesBadAt;
    private Long primaryShelfLifeOptionId;

    public static ItemStackShelfLifeOptionDto fromModel(ItemStackShelfLifeOption source) {

        if(source == null) return null;

        ItemStackShelfLifeOptionDto destination = new ItemStackShelfLifeOptionDto();
        destination.setId(source.getId());
        destination.setStorageMode(source.getStorageMode());
        destination.setShelfLifePresentation(source.getShelfLifePresentation());
        destination.setStartCountingFrom(source.getStartCountingFrom());
        destination.setActive(source.isActive());
        destination.setItemStackGoesBadAt(source.getItemStackGoesBadAt());

        if(source.getPrimaryShelfLifeOption() != null)
            destination.setPrimaryShelfLifeOptionId(source.getPrimaryShelfLifeOption().getId());

        return destination;
    }
}
