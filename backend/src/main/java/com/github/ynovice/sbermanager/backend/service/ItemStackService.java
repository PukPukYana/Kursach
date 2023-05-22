package com.github.ynovice.sbermanager.backend.service;

import com.github.ynovice.sbermanager.backend.model.ItemStack;
import com.github.ynovice.sbermanager.backend.model.dto.request.CreateItemStackRequestDto;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface ItemStackService {

    List<ItemStack> findAllActiveByOwner(boolean active, OAuth2User oAuth2User);

    ItemStack findByIdAndOwner(Long id, OAuth2User oAuth2User);

    ItemStack createItemStack(CreateItemStackRequestDto requestDto, OAuth2User oAuth2User);

    void updateItemStacks(OAuth2User oAuth2User);

    void deleteById(Long id, OAuth2User oAuth2User);

    void openItemStack(Long id, OAuth2User oAuth2User);

    void closeItemStack(Long id, OAuth2User oAuth2User);

    void archiveItemStack(Long id, OAuth2User oAuth2User);

    void unarchiveItemStack(Long id, OAuth2User oAuth2User);
}
