package com.usetech.dvente.services.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${jwt.cookie.max-age:3600}")
    private int accessTokenMaxAge;

    @Value("${jwt.cookie.refresh-max-age:86400}")
    private int refreshTokenMaxAge;

    @Value("${jwt.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${jwt.cookie.domain:}")
    private String cookieDomain;

    @Value("${jwt.cookie.same-site:Strict}")
    private String sameSite;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, "access_token", token, accessTokenMaxAge);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, "refresh_token", token, refreshTokenMaxAge);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        if (!cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        cookie.setAttribute("SameSite", sameSite);
        response.addCookie(cookie);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath("/");

        if (!cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        cookie.setAttribute("SameSite", sameSite);
        response.addCookie(cookie);
    }
}
