package com.github.ynovice.sbermanager.sbermarketapiclient;

import com.github.ynovice.sbermanager.sbermarketapiclient.exception.AuthorizationException;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class SbermarketApiClientV3 extends SbermarketApiClient {

    private final static String BASE_URL = "https://sbermarket.ru";
    private final static String BASE_API_URL = BASE_URL + "/api";
    private final static String DOMAIN = "sbermarket.ru";

    private final static String INSTAMART_SESSION_COOKIE_NAME = "_Instamart_session";

    private final static Duration DEFAULT_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

    private final String[] defaultHeaders = new String[] {
            "Accept", "application/json, text/plain, */*",
            "Accept-Encoding", "UTF-8",
            "api-version", "3.0",
            "client-token", "7ba97b6f4049436dab90c789f946ee2f"
    };

    private final ModelMapper modelMapper;

    /*
        Fields that are initialized inside the constructor
     */
    private final UnsuccessfulHttpResponseExceptionFactory<String> exceptionFactory;
    private final HttpClient httpClient;
    private final CookieStore cookieStore;

    public SbermarketApiClientV3(ModelMapper modelMapper) {
        this(modelMapper, null);
    }

    public SbermarketApiClientV3(ModelMapper modelMapper, String instamartSession) {

        Objects.requireNonNull(modelMapper, "ModelMapper cannot be null.");

        this.modelMapper = modelMapper;

        exceptionFactory = new UnsuccessfulHttpResponseExceptionFactoryImpl(modelMapper);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        cookieStore = cookieManager.getCookieStore();

        if(instamartSession != null) {
            try {
                cookieStore.add(new URI(DOMAIN), buildInstamartCookie(instamartSession));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        httpClient = HttpClient
                .newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(DEFAULT_TIMEOUT)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Override
    public Store getStoreById(int storeId) throws IOException, InterruptedException {

        String finalUrl = BASE_API_URL + "/stores/" + storeId;
        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);
        return modelMapper.map(httpResponse.body(), Store.class, "store");
    }

    @Override
    public Retailer getRetailerById(int retailerId) throws IOException, InterruptedException {

        String finalUrl = BASE_API_URL + "/retailers/" + retailerId;
        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);
        return modelMapper.map(httpResponse.body(), Retailer.class, "retailer");
    }

    @Override
    public CategoriesResponseBody getCategoriesByStoreId(int storeId) throws IOException, InterruptedException {

        String finalUrl = BASE_API_URL + "/stores/" + storeId + "/categories";
        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);
        return modelMapper.map(httpResponse.body(), CategoriesResponseBody.class);
    }

    @Override
    public ProductsResponseBody getProductsByStoreIdAndCategorySlug(int storeId,
                                                                    String categorySlug,
                                                                    int page,
                                                                    int perPage,
                                                                    String sort)
            throws IOException, InterruptedException {

        if(sort == null || sort.isBlank()) {
            sort = "popularity";
        }

        categorySlug = urlEncode(categorySlug);
        sort = urlEncode(sort);

        String finalUrl = String.format(
                BASE_API_URL + "/v3/stores/%d/products?tid=%s&page=%d&per_page=%d&sort=%s",
                storeId, categorySlug, page, perPage, sort
        );

        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);

        return modelMapper.map(httpResponse.body(), ProductsResponseBody.class);
    }

    @Override
    public ProductDetailedInfoResponseBody getProductByIdAndStoreId(int productId, int storeId)
            throws IOException, InterruptedException {

        String finalUrl = String.format(
                BASE_API_URL + "/v3/stores/%d/products/%d",
                storeId, productId
        );

        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);

        return modelMapper.map(httpResponse.body(), ProductDetailedInfoResponseBody.class);
    }

    @Override
    public ProductsResponseBody getProductsByStoreIdAndQuery(int storeId,
                                                             String query,
                                                             int page,
                                                             int perPage,
                                                             String sort)
            throws IOException, InterruptedException {

        if(sort == null || sort.isBlank()) {
            sort = "popularity";
        }

        query = urlEncode(query);
        sort = urlEncode(sort);

        String finalUrl = String.format(
                BASE_API_URL + "/v3/stores/%d/products?q=%s&page=%d&per_page=%d&sort=%s",
                storeId, query, page, perPage, sort
        );

        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl);

        return modelMapper.map(httpResponse.body(), ProductsResponseBody.class);
    }

    @Override
    public SendConfirmationCodeResponseBody sendPhoneConfirmationCode(String aesEncryptedPhoneNumber) throws IOException, InterruptedException {

        String finalUrl = BASE_API_URL + "/phone_confirmations";

        JSONObject requestBody = new JSONObject();
        requestBody.put("phone", aesEncryptedPhoneNumber);

        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl, "POST", requestBody.toString());

        return modelMapper.map(httpResponse.body(), SendConfirmationCodeResponseBody.class);
    }

    @Override
    public ConfirmPhoneNumberResponseBody confirmPhoneNumber(String phoneNumber,
                                                             String confirmationCode,
                                                             boolean b2b,
                                                             boolean promoTermsAccepted)
            throws IOException, InterruptedException {

        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        Objects.requireNonNull(phoneNumber, "Phone confirmation code cannot be null");

        phoneNumber = urlEncode(phoneNumber);

        String finalUrl = BASE_API_URL + "/phone_confirmations/" + phoneNumber;

        JSONObject requestBody = new JSONObject();
        requestBody.put("b2b", b2b);
        requestBody.put("phone_confirmation_code", confirmationCode);
        requestBody.put("promo_terms_accepted", promoTermsAccepted);

        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl, "PUT", requestBody.toString());

        return modelMapper.map(httpResponse.body(), ConfirmPhoneNumberResponseBody.class);
    }

    @Override
    public ShipmentsPage getShipments(long userId, int page) throws IOException, InterruptedException {

        String instamartSession = getInstamartSession();

        if(instamartSession == null) {
            throw new AuthorizationException("No Instamart session: in order to receive information on shipments, " +
                    "the customer must be authorized");
        }

        String finalUrl = String.format(
                BASE_API_URL + "/users/%d/shipments?page=%s",
                userId, page
        );

        String cookieValue = INSTAMART_SESSION_COOKIE_NAME + "=" + instamartSession;
        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl, "GET", "", List.of(cookieValue));

        return modelMapper.map(httpResponse.body(), ShipmentsPage.class);
    }

    @Override
    public LineItemsResponseBody getLineItemsByShipmentNumber(String shipmentNumber, int page, int perPage)
            throws IOException, InterruptedException {

        Objects.requireNonNull(shipmentNumber, "Shipment number cannot be null");

        String instamartSession = getInstamartSession();

        if(instamartSession == null) {
            throw new AuthorizationException("No Instamart session: in order to receive information on shipments, " +
                    "the customer must be authorized");
        }

        String finalUrl = String.format(
                BASE_API_URL + "/shipments/%s/assembly_items?page=%d&per_page=%d",
                shipmentNumber, page, perPage
        );

        String cookieValue = INSTAMART_SESSION_COOKIE_NAME + "=" + instamartSession;
        HttpResponse<String> httpResponse = sendHttpRequest(finalUrl, "GET", "", List.of(cookieValue));

        return modelMapper.map(httpResponse.body(), LineItemsResponseBody.class);
    }

    @Override
    public String getInstamartSession() {

        return cookieStore.getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals(INSTAMART_SESSION_COOKIE_NAME))
                .findFirst()
                .map(HttpCookie::getValue)
                .orElse(null);
    }


    private HttpResponse<String> sendHttpRequest(@NonNull String finalUrl)
            throws IOException, InterruptedException {

       return sendHttpRequest(finalUrl, "GET", "", Collections.emptyList());
    }

    private HttpResponse<String> sendHttpRequest(@NonNull String finalUrl,
                                                 @NonNull String method,
                                                 @NonNull String body
                                                 )
            throws IOException, InterruptedException {

        return sendHttpRequest(finalUrl, method, body, Collections.emptyList());
    }

    private HttpResponse<String> sendHttpRequest(@NonNull String finalUrl,
                                                 @NonNull String method,
                                                 @NonNull String body,
                                                 List<String> cookies)
            throws IOException, InterruptedException {

        HttpRequest.BodyPublisher bodyPublisher = body.length() == 0 ?
                HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createUri(finalUrl))
                .headers(defaultHeaders)
                .method(method, bodyPublisher);

        if(body.length() > 0) {
            requestBuilder.header("Content-Length", String.valueOf(body.length()));
            requestBuilder.header("Content-Type", "application/json");
        }

        cookies.forEach(value -> requestBuilder.header("Cookie", value));

        HttpRequest httpRequest = requestBuilder.build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if(httpResponse.statusCode() != 200) {
            throw exceptionFactory.nexException(httpResponse);
        }

        return httpResponse;
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private URI createUri(String url) {

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
            }
        return uri;
    }

    private HttpCookie buildInstamartCookie(String instamartSession) {

        HttpCookie httpCookie = new HttpCookie(INSTAMART_SESSION_COOKIE_NAME, instamartSession);
        httpCookie.setDomain(DOMAIN);
        httpCookie.setPath("/");
        httpCookie.setSecure(false);

        return httpCookie;
    }
}