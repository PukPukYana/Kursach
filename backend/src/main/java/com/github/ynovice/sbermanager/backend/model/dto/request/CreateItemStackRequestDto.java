package com.github.ynovice.sbermanager.backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemStackRequestDto {

    private String name;
    private String amountPerPack;
    private Integer count;
    private String imageUrl;
    private String description;

    private Integer placedAtDay;
    private Integer placedAtMonth;
    private Integer placedAtYear;

    private String primaryStorageMode;
    private String primaryShelfLifePresentation;

    private String afterOpeningStorageMode;
    private String afterOpeningShelfLifePresentation;
}
