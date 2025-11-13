
package com.usetech.dvente.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.repositories.UserRepository;
import com.usetech.dvente.requests.LoginRequest;
import com.usetech.dvente.services.auth.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NAME = "Test User";

    private String testEmail;

    @BeforeEach
    void setUp() {
        // Générer un email unique pour chaque test
        testEmail = "test-" + UUID.randomUUID() + "@example.com";

        User testUser = new User();
        testUser.setName(TEST_NAME);
        testUser.setEmail(testEmail);
        testUser.setPassword(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        testUser.setEmailConfirmed(true);
        testUser = userRepository.saveAndFlush(testUser);
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(TEST_PASSWORD);

        // Act & Assert
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value(testEmail))
                .andExpect(jsonPath("$.user.name").value(TEST_NAME))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        // Vérifier que les tokens sont valides
        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("token");
        assertThat(responseContent).contains("refreshToken");

        // Vérifier les cookies
        Cookie accessTokenCookie = result.getResponse().getCookie("access_token");
        Cookie refreshTokenCookie = result.getResponse().getCookie("refresh_token");

        assertThat(accessTokenCookie).isNotNull();
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(accessTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
    }
}