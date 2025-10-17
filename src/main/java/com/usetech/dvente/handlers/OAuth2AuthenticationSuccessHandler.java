package com.usetech.dvente.handlers;

import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.services.auth.CookieService;
import com.usetech.dvente.services.auth.OAuth2UserService;
import com.usetech.dvente.services.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");

            User user = oAuth2UserService.processOAuth2User(oAuth2User);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            cookieService.addAccessTokenCookie(response, accessToken);
            cookieService.addRefreshTokenCookie(response, refreshToken);

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();

            log.info("Redirecting to: {}", targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Error during OAuth2 authentication success handling", e);

            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/login")
                    .queryParam("error", "authentication_processing_failed")
                    .queryParam("message", e.getMessage())
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}