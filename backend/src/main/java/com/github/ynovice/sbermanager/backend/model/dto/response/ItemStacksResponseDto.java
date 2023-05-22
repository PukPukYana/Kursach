package com.github.ynovice.sbermanager.backend.model.dto.response;

import com.github.ynovice.sbermanager.backend.model.ItemStack;
import com.github.ynovice.sbermanager.backend.model.dto.ItemStackDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemStacksResponseDto {

    private List<ItemStackDto> itemStacks;

    public ItemStacksResponseDto(List<ItemStack> originalItemStacksList) {
        itemStacks = originalItemStacksList
                .stream()
                .map(ItemStackDto::fromModel)
                .toList();
    }
}
