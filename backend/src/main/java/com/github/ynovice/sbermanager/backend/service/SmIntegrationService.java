package com.github.ynovice.sbermanager.backend.service;

import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.LineItem;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.Product;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.SendConfirmationCodeResponseBody;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.Shipment;
import lombok.NonNull;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;

public interface SmIntegrationService {

    SendConfirmationCodeResponseBody sendPhoneConfirmationCode(String encryptedPhone, OAuth2User principal);

    void confirmPhoneNumber(String rawPhone,
                            String encryptedPhone,
                            String confirmationCode,
                            OAuth2User principal);

    List<Shipment> getShipments(User user);

    List<LineItem> getLineItems(String shipmentNumber, User user);

    Optional<Product> getProductByStoreIdAndRetailerSku(int storeId, @NonNull String retailerSku);
}
