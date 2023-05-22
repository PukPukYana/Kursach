package com.github.ynovice.sbermanager.backend.service.impl;

import com.github.ynovice.sbermanager.backend.exception.BadRequestException;
import com.github.ynovice.sbermanager.backend.exception.EntityNotFoundException;
import com.github.ynovice.sbermanager.backend.exception.InvalidObjectException;
import com.github.ynovice.sbermanager.backend.model.ItemStack;
import com.github.ynovice.sbermanager.backend.model.ItemStackShelfLifeOption;
import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.backend.model.dto.request.CreateItemStackRequestDto;
import com.github.ynovice.sbermanager.backend.model.validation.ItemStackShelfLifeOptionValidator;
import com.github.ynovice.sbermanager.backend.model.validation.ItemStackValidator;
import com.github.ynovice.sbermanager.backend.repository.ItemStackRepository;
import com.github.ynovice.sbermanager.backend.service.ItemStackService;
import com.github.ynovice.sbermanager.backend.service.SmIntegrationService;
import com.github.ynovice.sbermanager.backend.service.UserService;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.LineItem;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.Product;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.Shipment;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemStackServiceImpl implements ItemStackService {

    private final UserService userService;
    private final SmIntegrationService smIntegrationService;

    private final ItemStackRepository itemStackRepository;

    private final ItemStackValidator itemStackValidator;
    private final ItemStackShelfLifeOptionValidator itemStackShelfLifeOptionValidator;

    @Override
    public List<ItemStack> findAllActiveByOwner(boolean active, OAuth2User oAuth2User) {

        User owner = userService.getUserByPrincipal(oAuth2User);

        return itemStackRepository.findAllByActiveAndOwner(active, owner)
                .stream()
                .sorted((o1, o2) -> {

                    if(o1.getSecondsUntilGoesBad() == null) {
                        return 1;
                    }

                    if(o2.getSecondsUntilGoesBad() == null) {
                        return -1;
                    }

                    return o1.getSecondsUntilGoesBad().compareTo(o2.getSecondsUntilGoesBad());
                })
                .toList();
    }

    @Override
    public ItemStack findByIdAndOwner(Long id, OAuth2User oAuth2User) {

        User owner = userService.getUserByPrincipal(oAuth2User);

        return itemStackRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Товар с id \"%d\" не найден у пользователя %s", id, owner.getUsername())
                ));
    }

    @Override
    public ItemStack createItemStack(CreateItemStackRequestDto requestDto, OAuth2User oAuth2User) {

        User owner = userService.getUserByPrincipal(oAuth2User);

        ItemStack itemStack = new ItemStack();

        itemStack.setName(requestDto.getName());
        itemStack.setAmountPerPack(requestDto.getAmountPerPack());
        itemStack.setCount(requestDto.getCount());
        itemStack.setImageUrl(requestDto.getImageUrl());
        itemStack.setDescription(requestDto.getDescription());

        itemStack.setPlacedAt(
                buildPlacedAt(
                        requestDto.getPlacedAtDay(),
                        requestDto.getPlacedAtMonth(),
                        requestDto.getPlacedAtYear()));

        List<FieldError> validationErrors = new ArrayList<>(validate(itemStack));

        String primaryStorageMode = requestDto.getPrimaryStorageMode();
        String primaryShelfLifePresentation = requestDto.getPrimaryShelfLifePresentation();

        if(primaryStorageMode != null || primaryShelfLifePresentation != null) {
            ItemStackShelfLifeOption primaryShelfLifeOption =
                    new ItemStackShelfLifeOption(primaryStorageMode, primaryShelfLifePresentation);
            primaryShelfLifeOption.activate(itemStack.getPlacedAt());
            primaryShelfLifeOption.setItemStack(itemStack);
            itemStack.setPrimaryShelfLifeOption(primaryShelfLifeOption);
            validationErrors.addAll(validateShelfLifeOption(primaryShelfLifeOption));
        }

        String afterOpeningStorageMode = requestDto.getAfterOpeningStorageMode();
        String afterOpeningShelfLifePresentation = requestDto.getAfterOpeningShelfLifePresentation();

        if(afterOpeningStorageMode != null || afterOpeningShelfLifePresentation != null) {
            ItemStackShelfLifeOption afterOpeningShelfLifeOption =
                    new ItemStackShelfLifeOption(afterOpeningStorageMode, afterOpeningShelfLifePresentation);
            afterOpeningShelfLifeOption.setPrimaryShelfLifeOption(itemStack.getPrimaryShelfLifeOption());
            afterOpeningShelfLifeOption.setItemStack(itemStack);
            itemStack.setAfterOpeningShelfLifeOption(afterOpeningShelfLifeOption);
            validationErrors.addAll(validateShelfLifeOption(afterOpeningShelfLifeOption));
        }

        if(!validationErrors.isEmpty()) {
            throw new InvalidObjectException(validationErrors);
        }

        itemStack.setOwner(owner);
        itemStack.setActive(true);

        itemStackRepository.saveAndFlush(itemStack);

        return itemStack;
    }

    @Override
    public void updateItemStacks(@NonNull OAuth2User oAuth2User) {

        User owner = userService.getUserByPrincipal(oAuth2User);

        if(owner.getSmAuthData() == null) {
            throw new BadRequestException("Привяжите аккаунт со Сбермаркета в профиле");
        }

        List<ItemStack> savedItems = itemStackRepository.findAllByOwner(owner);

        List<Shipment> shipments = smIntegrationService.getShipments(owner);

        for(Shipment shipment : shipments) {

            List<LineItem> lineItems = smIntegrationService.getLineItems(shipment.number(), owner)
                    .stream()
                    .filter(lineItem -> !itemStackPresentBySmSku(savedItems, lineItem.sku()))
                    .toList();

            for(LineItem lineItem : lineItems) {

                ItemStack itemStack = new ItemStack();
                itemStack.setOwner(owner);
                itemStack.setPlacedAt(shipment.shippedAt() != null ? shipment.shippedAt() : ZonedDateTime.now());
                itemStack.setActive(true);

                injectDataToItemStack(itemStack, lineItem);

                smIntegrationService
                        .getProductByStoreIdAndRetailerSku(lineItem.storeId(), lineItem.retailerSku())
                        .ifPresent(
                                product -> injectDataToItemStack(itemStack, product)
                        );

                itemStackRepository.save(itemStack);
            }
        }
    }

    @Override
    public void deleteById(Long id, OAuth2User oAuth2User) {

        User owner = userService.getUserByPrincipal(oAuth2User);

        ItemStack itemStack = itemStackRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Товар с id \"%d\" не найден у пользователя %s", id, owner.getUsername())
                ));

        itemStackRepository.delete(itemStack);
    }

    @Override
    public void openItemStack(Long id, OAuth2User oAuth2User) {

        ItemStack itemStack = findByIdAndOwner(id, oAuth2User);

        if(itemStack.isOpened() == null) throw new BadRequestException("Товар не может быть вскрыт");
        if(itemStack.isOpened()) throw new BadRequestException("Товар уже вскрыт");

        itemStack.open();
        itemStackRepository.save(itemStack);
    }

    @Override
    public void closeItemStack(Long id, OAuth2User oAuth2User) {

        ItemStack itemStack = findByIdAndOwner(id, oAuth2User);

        if(itemStack.isOpened() == null) throw new BadRequestException("Товар не может быть вскрыт или закрыт");
        if(!itemStack.isOpened()) throw new BadRequestException("Товар не вскрыт");

        itemStack.close();
        itemStackRepository.save(itemStack);
    }

    @Override
    public void archiveItemStack(Long id, OAuth2User oAuth2User) {

        ItemStack itemStack = findByIdAndOwner(id, oAuth2User);

        if(!itemStack.getActive()) throw new BadRequestException("Товар уже находится в архиве");

        itemStack.setActive(false);
        itemStackRepository.save(itemStack);
    }

    @Override
    public void unarchiveItemStack(Long id, OAuth2User oAuth2User) {

        ItemStack itemStack = findByIdAndOwner(id, oAuth2User);

        if(itemStack.getActive()) throw new BadRequestException("Товар не находится в архиве");

        itemStack.setActive(true);
        itemStackRepository.save(itemStack);
    }

    private boolean itemStackPresentBySmSku(@NonNull List<ItemStack> itemStacks, String smSku) {
        return itemStacks
                .stream()
                .anyMatch(item -> Objects.equals(item.getSmSku(), smSku));
    }

    private List<FieldError> validateField(@NonNull ItemStack itemStack, @NonNull String fieldName) {
        return validate(itemStack)
                .stream()
                .filter(fieldError -> fieldError.getField().equals(fieldName))
                .toList();
    }

    private List<FieldError> validate(@NonNull ItemStack itemStack) {
        DataBinder dataBinder = new DataBinder(itemStack);
        dataBinder.addValidators(itemStackValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult().getFieldErrors();
    }

    // todo move to a different place
    private List<FieldError> validateShelfLifeOption(@NonNull ItemStackShelfLifeOption itemStackShelfLifeOption) {
        DataBinder dataBinder = new DataBinder(itemStackShelfLifeOption);
        dataBinder.addValidators(itemStackShelfLifeOptionValidator);
        dataBinder.validate();
        return dataBinder.getBindingResult().getFieldErrors();
    }

    private void injectDataToItemStack(@NonNull ItemStack destination, @NonNull LineItem source) {
        destination.setName(source.name());
        destination.setSmSku(source.sku());
        destination.setCount(source.foundPcs());
        destination.setAmountPerPack(StringUtils.hasText(source.quantity()) ? source.quantity() : source.humanVolume());
        destination.setImageUrl(source.image().smallUrl());
    }

    private void injectDataToItemStack(@NonNull ItemStack destination, @NonNull Product source) {

        if(StringUtils.hasText(source.description())) {
            destination.setDescription(source.description());
        }

        getAnyShelfLifeOption(source, true)
                .ifPresent(foundPrimaryShelfLifeOption -> {
                    foundPrimaryShelfLifeOption.setItemStack(destination);
                    foundPrimaryShelfLifeOption.activate(destination.getPlacedAt());
                    destination.setPrimaryShelfLifeOption(foundPrimaryShelfLifeOption);
                });

        getAnyShelfLifeOption(source, false)
                .ifPresent(foundAfterOpeningShelfLifeOption -> {
                    foundAfterOpeningShelfLifeOption.setItemStack(destination);
                    foundAfterOpeningShelfLifeOption.setPrimaryShelfLifeOption(destination.getPrimaryShelfLifeOption());
                    destination.setAfterOpeningShelfLifeOption(foundAfterOpeningShelfLifeOption);
                });
    }

    private Optional<ItemStackShelfLifeOption> getAnyShelfLifeOption(@NonNull Product product, boolean primary) {

        List<Product.Property> propertiesOfAppropriateStorageMods = product.properties()
                .stream()
                .filter(property -> property.presentation().toLowerCase().startsWith("условия хранения"))
                .filter(property ->
                        (primary && !property.value().toLowerCase().startsWith("после вскрытия")) ||
                                (!primary && property.value().toLowerCase().startsWith("после вскрытия"))
                )
                .toList();

        if(propertiesOfAppropriateStorageMods.isEmpty()) {
            return Optional.empty();
        }

        for(Product.Property property : propertiesOfAppropriateStorageMods) {
            Optional<String> shelfLifePresentation = getAccordingShelfLifePresentation(product, property);

            if(shelfLifePresentation.isPresent()) {
                return Optional.of(new ItemStackShelfLifeOption(property.value(), shelfLifePresentation.get()));
            }
        }

        return Optional.of(
                new ItemStackShelfLifeOption(
                        propertiesOfAppropriateStorageMods.get(0).value()
                )
        );
    }

    private Optional<String> getAccordingShelfLifePresentation(@NonNull Product product,
                                                               @NonNull Product.Property storageModeProp) {
        String propPresentation = storageModeProp.presentation();
        String commonPropNamesEnding = propPresentation.substring(propPresentation.length() - 2);

        return product.properties()
                .stream()
                .filter(property -> property.presentation().toLowerCase().startsWith("срок хранения"))
                .filter(property -> property.presentation().endsWith(commonPropNamesEnding))
                .map(Product.Property::value)
                .findFirst();
    }

    private ZonedDateTime buildPlacedAt(Integer placedAtDay, Integer placedAtMonth, Integer placedAtYear) {

        if(placedAtDay == null || placedAtMonth == null || placedAtYear == null) {
            return null;
        }

        if(placedAtYear < 1) {
            return null;
        }

        try {
            return ZonedDateTime.of(
                    placedAtYear,
                    placedAtMonth,
                    placedAtDay,
                    0, 0, 0, 0, TimeZone.getDefault().toZoneId());
        } catch (DateTimeException e) {
            return null;
        }
    }
}