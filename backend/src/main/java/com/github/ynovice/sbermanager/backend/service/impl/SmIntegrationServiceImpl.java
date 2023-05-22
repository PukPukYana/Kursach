package com.github.ynovice.sbermanager.backend.service.impl;

import com.github.ynovice.sbermanager.backend.exception.InvalidFieldsException;
import com.github.ynovice.sbermanager.backend.exception.NotAuthorizedException;
import com.github.ynovice.sbermanager.backend.exception.SmClientInteractionException;
import com.github.ynovice.sbermanager.backend.model.Role;
import com.github.ynovice.sbermanager.backend.model.SmAuthData;
import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.backend.repository.SmAuthDataRepository;
import com.github.ynovice.sbermanager.backend.service.SmIntegrationService;
import com.github.ynovice.sbermanager.backend.service.UserService;
import com.github.ynovice.sbermanager.sbermarketapiclient.ModelMapper;
import com.github.ynovice.sbermanager.sbermarketapiclient.ModelMapperJackson;
import com.github.ynovice.sbermanager.sbermarketapiclient.SbermarketApiClient;
import com.github.ynovice.sbermanager.sbermarketapiclient.SbermarketApiClientV3;
import com.github.ynovice.sbermanager.sbermarketapiclient.exception.UnsuccessfulHttpResponseException;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.*;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmIntegrationServiceImpl implements SmIntegrationService {

    private final UserService userService;
    private final SmAuthDataRepository smAuthDataRepository;

    private final ModelMapper modelMapper = new ModelMapperJackson();

    @Override
    public SendConfirmationCodeResponseBody sendPhoneConfirmationCode(String encryptedPhone, OAuth2User principal) {

        User user = userService.getUserByPrincipal(principal);
        checkRegistrationCompletion(user);

        SbermarketApiClient sbermarketApiClient = new SbermarketApiClientV3(modelMapper);

        SendConfirmationCodeResponseBody sendConfirmationCodeResponseBody;
        try {
            sendConfirmationCodeResponseBody = sbermarketApiClient.sendPhoneConfirmationCode(encryptedPhone);
        } catch (IOException | InterruptedException e) {
            throw new SmClientInteractionException("Что-то пошло не так, повторите попытку позже");
        } catch (UnsuccessfulHttpResponseException e) {
            throw new SmClientInteractionException(e);
        }

        return sendConfirmationCodeResponseBody;
    }

    @Transactional
    @Override
    public void confirmPhoneNumber(String rawPhone,
                                   String encryptedPhone,
                                   String confirmationCode,
                                   OAuth2User principal) {


        User user = userService.getUserByPrincipal(principal);
        checkRegistrationCompletion(user);

        List<ObjectError> phoneValidationResult  = userService.validatePhone(rawPhone);
        if(!phoneValidationResult.isEmpty()) {
            throw new InvalidFieldsException(phoneValidationResult);
        }

        if(rawPhone.length() == 12) {
            rawPhone = rawPhone.substring(1);
        } else {
            rawPhone = rawPhone.replaceFirst("8", "7");
        }

        SbermarketApiClient sbermarketApiClient = new SbermarketApiClientV3(modelMapper);

        ConfirmPhoneNumberResponseBody responseBody;
        try {
            responseBody = sbermarketApiClient.confirmPhoneNumber(
                    rawPhone,
                    confirmationCode,
                    false,
                    false);
        } catch (IOException | InterruptedException e) {
            throw new SmClientInteractionException("Что-то пошло не так, повторите попытку позже");
        } catch (UnsuccessfulHttpResponseException e) {
            throw new SmClientInteractionException(e);
        }

        SmAuthData smAuthData = new SmAuthData();
        smAuthData.setInstamartSession(sbermarketApiClient.getInstamartSession());
        smAuthData.setPhone(rawPhone);
        smAuthData.setEncryptedPhone(encryptedPhone);
        smAuthData.setUser(user);
        smAuthData.setSmUserId(responseBody.user().id());

        smAuthDataRepository.deleteAllByUser(user);
        smAuthDataRepository.save(smAuthData);

        user.setSmAuthData(smAuthData);
        userService.save(user);
    }

    @Override
    public List<Shipment> getShipments(User user) {

        String instamartSession = user.getSmAuthData().getInstamartSession();
        Long smUserId = user.getSmAuthData().getSmUserId();

        SbermarketApiClient sbermarketApiClient = new SbermarketApiClientV3(modelMapper, instamartSession);

        List<Shipment> shipments = new ArrayList<>();

        int currentPage = 1;
        boolean nextPageExists;

        do {

            ShipmentsPage shipmentsPage;
            try {
                shipmentsPage = sbermarketApiClient.getShipments(smUserId, currentPage++);
            } catch (IOException | InterruptedException e) {
                throw new SmClientInteractionException("Что-то пошло не так, повторите попытку позже");
            } catch (UnsuccessfulHttpResponseException e) {
                throw new SmClientInteractionException(e);
            }

            shipments.addAll(shipmentsPage.shipments());

            nextPageExists = shipmentsPage.meta().nextPage() != null;

        } while (nextPageExists);

        return shipments;
    }

    @Override
    public List<LineItem> getLineItems(String shipmentNumber, User user) {

        checkRegistrationCompletion(user);

        String instamartSession = user.getSmAuthData().getInstamartSession();

        SbermarketApiClient sbermarketApiClient = new SbermarketApiClientV3(modelMapper, instamartSession);

        List<LineItem> lineItems = new ArrayList<>();

        int currentPage = 1;
        boolean nextPageExists;

        do {

            LineItemsResponseBody lineItemsResponseBody;
            try {
                lineItemsResponseBody = sbermarketApiClient.getLineItemsByShipmentNumber(
                        shipmentNumber,
                        currentPage,
                        20);
            } catch (IOException | InterruptedException e) {
                throw new SmClientInteractionException("Что-то пошло не так, повторите попытку позже");
            } catch (UnsuccessfulHttpResponseException e) {
                throw new SmClientInteractionException(e);
            }

            lineItems.addAll(lineItemsResponseBody.lineItems());

            nextPageExists = lineItemsResponseBody.meta().nextPage() != null;

        } while (nextPageExists);

        return lineItems;
    }

    @Override
    public Optional<Product> getProductByStoreIdAndRetailerSku(int storeId, @NonNull String retailerSku) {

        SbermarketApiClient sbermarketApiClient = new SbermarketApiClientV3(modelMapper);

        ProductDetailedInfoResponseBody productDetailedInfoResponseBody;
        try {
            productDetailedInfoResponseBody =
                    sbermarketApiClient.getProductByIdAndStoreId(Integer.parseInt(retailerSku), storeId);
        } catch (IOException | InterruptedException e) {
            throw new SmClientInteractionException("Что-то пошло не так, повторите попытку позже");
        } catch (UnsuccessfulHttpResponseException e) {

            if(e.getStatusCode() == 404) {
                return Optional.empty();
            }

            throw new SmClientInteractionException(e);
        }

        return Optional.of(productDetailedInfoResponseBody.product());
    }

    private void checkRegistrationCompletion(User user) {
        if(user.getRole() == Role.GUEST) {
            throw new NotAuthorizedException("Завершите регистрацию для выполнения этого действия");
        }
    }
}
