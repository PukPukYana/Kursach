package com.github.ynovice.sbermanager.backend.service.impl;

import com.github.ynovice.sbermanager.backend.exception.BadRequestException;
import com.github.ynovice.sbermanager.backend.exception.InternalServerError;
import com.github.ynovice.sbermanager.backend.exception.InvalidObjectException;
import com.github.ynovice.sbermanager.backend.model.*;
import com.github.ynovice.sbermanager.backend.model.validation.PhoneValidator;
import com.github.ynovice.sbermanager.backend.model.validation.UserValidator;
import com.github.ynovice.sbermanager.backend.repository.UserRepository;
import com.github.ynovice.sbermanager.backend.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;
    private final PhoneValidator phoneValidator;

    @Override
    public User getUserByPrincipal(@NonNull OAuth2User oAuth2User) {

        String externalId = oAuth2User.getName();
        AuthServer authServer = getAuthServer(oAuth2User);

        return userRepository
                .findByExternalIdAndAuthServer(externalId, authServer)
                .orElseThrow(
                        () -> new InternalServerError(
                                "Что-то пошло не так, попробуйте войти в аккаунт заново"
                        )
                );
    }

    @Override
    public User registerIfNotRegistered(@NonNull OAuth2User oAuth2User) {

        String externalId = oAuth2User.getName();
        AuthServer authServer = getAuthServer(oAuth2User);

        return userRepository
                .findByExternalIdAndAuthServer(externalId, authServer)
                .orElseGet(() -> {

                    UserOauth2CredentialId credentialId = new UserOauth2CredentialId();
                    credentialId.setExternalId(externalId);
                    credentialId.setAuthServer(authServer);

                    UserOauth2Credential credential = new UserOauth2Credential();
                    credential.setId(credentialId);

                    User user = new User();
                    credentialId.setUser(user);
                    user.setRole(Role.GUEST);
                    user.setOauth2Credentials(Collections.singleton(credential));

                    userRepository.saveAndFlush(user);

                    return user;
                });
    }

    @Override
    public void completeUserRegistration(@NonNull OAuth2User oAuth2User, String username, String email) {

        User user = getUserByPrincipal(oAuth2User);

        if(!user.getRole().equals(Role.GUEST)) {
            throw new BadRequestException("Вы уже зарегистрированы!");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Role.USER);

        List<FieldError> invalidFields = new ArrayList<>(validateUsername(username));
        invalidFields.addAll(validateEmail(email));

        if(!invalidFields.isEmpty()) {
            throw new InvalidObjectException(invalidFields);
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        OAuth2AuthenticationToken oldAuthentication = (OAuth2AuthenticationToken) securityContext.getAuthentication();
        securityContext.setAuthentication(authCopyWithCurrentRole(oldAuthentication, user));

        userRepository.save(user);
    }

    @Override
    public List<FieldError> validateUsername(String username) {

        User user = new User();
        user.setUsername(username);

        DataBinder dataBinder = new DataBinder(user);
        dataBinder.addValidators(userValidator);
        dataBinder.validate();

        return dataBinder.getBindingResult().getFieldErrors("username");
    }

    @Override
    public List<FieldError> validateEmail(String email) {

        User user = new User();
        user.setEmail(email);

        DataBinder dataBinder = new DataBinder(user);
        dataBinder.addValidators(userValidator);
        dataBinder.validate();

        return dataBinder.getBindingResult().getFieldErrors("email");
    }

    @Override
    public List<ObjectError> validatePhone(String phone) {
        DataBinder dataBinder = new DataBinder(phone, "phone");
        dataBinder.addValidators(phoneValidator);
        dataBinder.validate();

        return dataBinder.getBindingResult().getAllErrors();
    }

    @Override
    public void save(@NonNull User user) {
        userRepository.save(user);
    }


    private AuthServer getAuthServer(@NonNull OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        if(attributes.containsKey("iss") && attributes.get("iss").toString().contains("google.com")) {
            return AuthServer.GOOGLE;
        } else if (attributes.containsKey("html_url")
                && attributes.get("html_url").toString().contains("github.com")) {
            return AuthServer.GITHUB;
        }

        throw new InternalServerError("Произошла ошибка при попытке распознать сервер авторизации");
    }

    private Authentication authCopyWithCurrentRole(OAuth2AuthenticationToken oldAuthentication, User user) {

        List<GrantedAuthority> newAuthorities =
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + user.getRole());

        OAuth2User oldPrincipal = oldAuthentication.getPrincipal();

        OAuth2User newPrincipal;

        if(oldPrincipal instanceof OidcUser) {
            newPrincipal = new DefaultOidcUser(newAuthorities, ((OidcUser) oldPrincipal).getIdToken());
        } else {

            String nameAttributeKey = oldPrincipal.getAttributes().entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == oldPrincipal.getName())
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();

            newPrincipal = new DefaultOAuth2User(newAuthorities, oldPrincipal.getAttributes(), nameAttributeKey);
        }

        OAuth2AuthenticationToken newAuthentication = new OAuth2AuthenticationToken(
                newPrincipal,
                newAuthorities,
                oldAuthentication.getAuthorizedClientRegistrationId());
        newAuthentication.setAuthenticated(oldAuthentication.isAuthenticated());

        return newAuthentication;
    }
}
