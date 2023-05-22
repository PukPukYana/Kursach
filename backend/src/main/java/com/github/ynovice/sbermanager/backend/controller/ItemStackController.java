package com.github.ynovice.sbermanager.backend.controller;

import com.github.ynovice.sbermanager.backend.model.dto.ItemStackDto;
import com.github.ynovice.sbermanager.backend.model.dto.request.CreateItemStackRequestDto;
import com.github.ynovice.sbermanager.backend.model.dto.response.ItemStacksResponseDto;
import com.github.ynovice.sbermanager.backend.service.ItemStackService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item_stack")
@AllArgsConstructor
public class ItemStackController {

    private final ItemStackService itemStackService;

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<ItemStacksResponseDto> findAllActive(@RequestParam(defaultValue = "true") boolean active,
                                                               @AuthenticationPrincipal OAuth2User oAuth2User) {

        return ResponseEntity.ok(
                new ItemStacksResponseDto(itemStackService.findAllActiveByOwner(active, oAuth2User))
        );
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    public ResponseEntity<ItemStackDto> findById(@PathVariable Long id,
                                                 @AuthenticationPrincipal OAuth2User oAuth2User) {
        return ResponseEntity.ok(ItemStackDto.fromModel(itemStackService.findByIdAndOwner(id, oAuth2User)));
    }

    @PostMapping
    public ResponseEntity<ItemStackDto> createItemStack(@RequestBody CreateItemStackRequestDto requestDto,
                                                        @AuthenticationPrincipal OAuth2User oAuth2User) {
        return ResponseEntity.ok(
                ItemStackDto.fromModel(itemStackService.createItemStack(requestDto, oAuth2User))
        );
    }

    @Secured("ROLE_USER")
    @PostMapping("/update")
    public ResponseEntity<Void> updateItemStacks(@AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.updateItemStacks(oAuth2User);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemStack(@PathVariable Long id,
                                                @AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.deleteById(id, oAuth2User);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/open")
    public ResponseEntity<Void> openItemStack(@PathVariable Long id, @AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.openItemStack(id, oAuth2User);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/close")
    public ResponseEntity<Void> closeItemStack(@PathVariable Long id, @AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.closeItemStack(id, oAuth2User);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveItemStack(@PathVariable Long id,
                                                 @AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.archiveItemStack(id, oAuth2User);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveItemStack(@PathVariable Long id,
                                                   @AuthenticationPrincipal OAuth2User oAuth2User) {
        itemStackService.unarchiveItemStack(id, oAuth2User);
        return ResponseEntity.ok().build();
    }
}
