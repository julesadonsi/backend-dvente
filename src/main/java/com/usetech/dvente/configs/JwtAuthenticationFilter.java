package com.usetech.dvente.configs;

import com.usetech.dvente.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/admin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;

        // Essayer d'extraire le JWT depuis le cookie en priorité
        jwt = extractJwtFromCookie(request);

        // Si pas de cookie, essayer le header Authorization
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.warn("Token expiré");
            clearAuthCookies(response);
            sendErrorResponse(response, 401, "token_expired", "Le token JWT a expiré", e.getClaims().getExpiration().toInstant().toString());
            return;

        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.warn("Token invalide");
            clearAuthCookies(response);
            sendErrorResponse(response, 400, "token_invalid", "Le token JWT est invalide", null);
            return;

        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.warn("Token non supporté");
            clearAuthCookies(response);
            sendErrorResponse(response, 400, "token_unsupported", "Le token JWT n'est pas supporté", null);
            return;

        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.warn("Signature invalide");
            clearAuthCookies(response);
            sendErrorResponse(response, 400, "token_invalid_signature", "La signature du token JWT est invalide", null);
            return;

        } catch (Exception e) {
            logger.error("Erreur authentification JWT : " + e.getMessage());
            clearAuthCookies(response);
            sendErrorResponse(response, 500, "internal_error", "Erreur interne lors de l'authentification JWT", null);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(refreshTokenCookie);
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String expiredAt
    )
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        String json = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\"%s}",
                error,
                message,
                expiredAt != null ? String.format(",\"expiredAt\":\"%s\"", expiredAt) : ""
        );

        response.getWriter().write(json);
    }
}