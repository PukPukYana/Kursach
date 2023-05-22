package com.github.ynovice.sbermanager.backend.model.dto;

import com.github.ynovice.sbermanager.backend.model.ItemStack;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ItemStackDto {

    private Long id;
    private String name;
    private String imageUrl;
    private String description;
    private Boolean active;
    private ZonedDateTime placedAt;
    private String smSku;

    private ItemStackShelfLifeOptionDto primaryShelfLifeOption;
    private ItemStackShelfLifeOptionDto afterOpeningShelfLifeOption;
    private Boolean opened;
    private Long secondsUntilGoesBad;
    private String goesBadInPresentation;

    private String amountPerPack;
    private Integer count;
    private Long ownerId;

    public static ItemStackDto fromModel(ItemStack source) {

        if(source == null) return null;

        ItemStackDto itemStackDto = new ItemStackDto();

        itemStackDto.setId(source.getId());
        itemStackDto.setName(source.getName());
        itemStackDto.setImageUrl(source.getImageUrl());
        itemStackDto.setDescription(source.getDescription());
        itemStackDto.setActive(source.getActive());
        itemStackDto.setPlacedAt(source.getPlacedAt());
        itemStackDto.setSmSku(source.getSmSku());

        itemStackDto.setPrimaryShelfLifeOption(
                ItemStackShelfLifeOptionDto.fromModel(source.getPrimaryShelfLifeOption())
        );
        itemStackDto.setAfterOpeningShelfLifeOption(
                ItemStackShelfLifeOptionDto.fromModel(source.getAfterOpeningShelfLifeOption())
        );
        itemStackDto.setOpened(source.isOpened());
        itemStackDto.setSecondsUntilGoesBad(source.getSecondsUntilGoesBad());
        itemStackDto.setGoesBadInPresentation(source.getGoesBadInPresentation());

        itemStackDto.setAmountPerPack(source.getAmountPerPack());
        itemStackDto.setCount(source.getCount());
        itemStackDto.setOwnerId(source.getOwnerId());

        return itemStackDto;
    }
}
