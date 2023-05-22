package com.github.ynovice.sbermanager.backend.config;

import com.github.ynovice.sbermanager.backend.security.CustomAuthenticationSuccessHandler;
import com.github.ynovice.sbermanager.backend.security.CustomCsrfTokenRequestHandler;
import com.github.ynovice.sbermanager.backend.security.CustomOauth2UserService;
import com.github.ynovice.sbermanager.backend.security.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final CustomOidcUserService customOidcUserService;
    private final CustomOauth2UserService customOauth2UserService;

    private final CustomCsrfTokenRequestHandler customCsrfTokenRequestHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().authenticated())
                .logout(l -> l
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint()))
                .csrf(c -> c
                        .csrfTokenRequestHandler(customCsrfTokenRequestHandler)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint().oidcUserService(customOidcUserService)
                        .userService(customOauth2UserService)
                        .and()
                        .successHandler(customAuthenticationSuccessHandler));

        return httpSecurity.build();
    }
}
