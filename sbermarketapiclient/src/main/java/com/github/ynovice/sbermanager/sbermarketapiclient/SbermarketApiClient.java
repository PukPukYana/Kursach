package com.github.ynovice.sbermanager.sbermarketapiclient;

import com.github.ynovice.sbermanager.sbermarketapiclient.model.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public abstract class SbermarketApiClient {

    /**
     * Sends http request to Sbermarket API and returns the detailed information about a store by its id.
     * Does not require authorization or authentication.
     * @param storeId The store id.
     * @return {@code Store} instance corresponding to the store with the given id.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
                           or if the body of http response from Sbermarket API can not be
                           mapped to the {@code Store} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract Store getStoreById(int storeId) throws IOException, InterruptedException;

    /**
     * Sends http request to Sbermarket API and returns the detailed information about a retailer by its id.
     * Does not require authorization or authentication.
     * @param retailerId The retailer id.
     * @return {@code Retailer} instance corresponding to the retailer with the given id.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
                           or if the body of http response from Sbermarket API can not be
                           mapped to the {@code Retailer} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract Retailer getRetailerById(int retailerId) throws IOException, InterruptedException;

    /**
     * Sends http request to Sbermarket API and returns an object containing lists of all the categories present in the
     * store with the given id.
     * Requires client authorization: "client-token" header must be present in a request.
     * @param storeId The store id;
     * @return {@code CategoriesResponseBody} instance containing the list of all the categories present in the
     *         store with the given id.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code CategoriesResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract CategoriesResponseBody getCategoriesByStoreId(int storeId) throws IOException, InterruptedException;

    /**
     * Sends http request to Sbermarket API and returns an object containing list of products present in the
     * store with the given id from the category with the given slug.
     * Requires client authorization: "client-token" header must be present in a request.
     * @param storeId The store id.
     * @param categorySlug The slug (tid) of the products' category.
     * @param page The number of the needed page.
     * @param perPage Amount of products per page.
     * @param sort The parameter by which sorting is performed and the sorting order.
     * @return {@code ProductsResponseBody} instance containing one page of products from the category with the given
     *         slug and meta information about pagination, products count, etc.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ProductsResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract ProductsResponseBody getProductsByStoreIdAndCategorySlug(int storeId,
                                                                             String categorySlug,
                                                                             int page,
                                                                             int perPage,
                                                                             String sort)
        throws IOException, InterruptedException;

    /**
     * Sends http request to Sbermarket API and returns the detailed information about a product by its id and the id
     * of its' store.
     * Requires client authorization: "client-token" header must be present in a request.
     * @param productId The product id.
     * @param storeId The id of the store in which the product is present.
     * @return {@code ProductDetailedInfoResponseBody} instance containing a detailed info about the needed product.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ProductDetailedInfoResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract ProductDetailedInfoResponseBody getProductByIdAndStoreId(int productId, int storeId)
            throws IOException, InterruptedException;

    /**
     * Sends http request to Sbermarket API and returns an object containing list of products present in the
     * store with the given id, matching the given query.
     * Requires client authorization: "client-token" header must be present in a request.
     * @param storeId The store id.
     * @param query The search query.
     * @param page The number of the needed page.
     * @param perPage Amount of products per page.
     * @param sort The parameter by which sorting is performed and the sorting order.
     * @return {@code ProductsResponseBody} instance containing one page of products matching the given query
     *         and meta information about pagination, products count, etc.
     * @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ProductsResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract ProductsResponseBody getProductsByStoreIdAndQuery(int storeId,
                                                                      String query,
                                                                      int page,
                                                                      int perPage,
                                                                      String sort)
        throws IOException, InterruptedException;

    /**
     * Sends an encrypted phone number to the sbermarket API so that the sbermarket server sends an SMS
     * with a one-time authorization code to the phone number.
     * The API client currently has no method for encrypting the phone number.
     * The encryption key is generated by javascript code on the sbermarket website. An encrypted phone
     * number has no expiration date. Once you encrypt a phone umber, you can always use it to perform
     * this method. At the moment you can find the encrypted version of your phone number by looking at the
     * query log in the developer tools of your browser after logging in to your account on the sbermarket website.
     *
     * @param aesEncryptedPhoneNumber AES encrypted phone number
     * @return {@code SendConfirmationCodeResponseBody} instance containing the lenght of the one-time code
     * @throws IOException @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code SendConfirmationCodeResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract SendConfirmationCodeResponseBody sendPhoneConfirmationCode(String aesEncryptedPhoneNumber)
            throws IOException, InterruptedException;

    /**
     * Sends a one-time phone number confirmation code to the sbermarket API. Used to get instamart session id.
     *
     * @param phoneNumber User's raw phone number
     * @param confirmationCode Phone number confirmation code
     * @param b2b False for individual users
     * @param promoTermsAccepted False if the user does not accept promo terms
     * @return Object containing the length of sent confirmation code
     * @throws IOException @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ConfirmPhoneNumberResponseBody} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract ConfirmPhoneNumberResponseBody confirmPhoneNumber(String phoneNumber,
                                                                      String confirmationCode,
                                                                      boolean b2b,
                                                                      boolean promoTermsAccepted)
            throws IOException, InterruptedException;

    /**
     * Sends a request to the sbermarket API to get the user's shipments history. The order history is divided
     * into several fixed-size pages. Additional information about the pages is contained in the {@code Meta} object.
     *
     * @param userId The user's id
     * @param page Required page number
     * @return One page of the user's shipments history
     * @throws IOException @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ShipmentsPage} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract ShipmentsPage getShipments(long userId, int page) throws IOException, InterruptedException;

    /**
     * Sends a request to the sbermarket API to get a list of line items in the shipment with the specified number.
     * The line items list is divided into several fixed-size pages. Additional information about the pages
     * is contained in the {@code Meta} object.
     *
     * @param shipmentNumber The shipment's number
     * @param page Required page number
     * @param perPage Amount of line items per page
     * @return One page of the shipment's line items
     * @throws IOException @throws IOException If an I/O error occurs when sending request or receiving response,
     *                     or if the body of http response from Sbermarket API can not be
     *                     mapped to the {@code ShipmentsPage} instance.
     * @throws InterruptedException If the operation of sending http request or receiving http response is interrupted.
     */
    public abstract LineItemsResponseBody getLineItemsByShipmentNumber(String shipmentNumber, int page, int perPage)
            throws IOException, InterruptedException;

    /**
     * Instamart session becomes available after successful execution of the {@code confirmPhoneNumber} method
     *
     * @return Current instamart session id of the user.
     */
    public abstract String getInstamartSession();
}
