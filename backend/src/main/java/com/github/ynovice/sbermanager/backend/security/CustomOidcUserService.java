package com.github.ynovice.sbermanager.backend.security;

import com.github.ynovice.sbermanager.backend.model.User;
import com.github.ynovice.sbermanager.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser trustedOidcUser = super.loadUser(userRequest);

        User user = userService.registerIfNotRegistered(trustedOidcUser);
        Set<GrantedAuthority> authorities = Set.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
        );

        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();

        if (StringUtils.hasText(userNameAttributeName)) {
            return new DefaultOidcUser(
                    authorities,
                    trustedOidcUser.getIdToken(),
                    trustedOidcUser.getUserInfo(),
                    userNameAttributeName);
        }

        return new DefaultOidcUser(
                authorities,
                trustedOidcUser.getIdToken(),
                trustedOidcUser.getUserInfo());
    }
}
