package org.leverx.ratingapp.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Role;
import org.leverx.ratingapp.models.enums.Status;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Registration API endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Integration tests for Registration API endpoints")
@Tag("integration")
public class RegistrationAndAuthenticationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private EmailService emailService;

    @Value("${admin.email}") private String adminEmail;
    @Value("${admin.password}") private String adminPassword;

    @Value("sheinnickolas@gmail.com") private String userEmail;
    @Value("qwerty") private String userPassword;
    @Value("John") private String userName;
    @Value("Deer") private String userLastName;

    /**
     * Tests authentication attempt of an unregistered user.
     * Expected result: Unauthorized status (401).
     */
    @Test
    @DisplayName("Check authentication of unregistered user")
    void testUnregisteredUserAuthEndpointFail() throws Exception {
        AuthenticationRequestDTO userAuthRequest =  AuthenticationRequestDTO
                .builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthRequest)))
                .andExpect(status().isUnauthorized());

    }

    /**
     * Tests that a regular user is denied access to admin panel endpoints.
     * Expected behavior:
     * - A seller (regular user) is registered and authenticated.
     * - The user attempts to access admin-only endpoints.
     * - All requests should return HTTP 403 Forbidden.
     */
    @Test
    @DisplayName("Check user access to admin panel")
    void testUserAccessToAdminPanelEndpointFail() throws Exception {
        // Step 1. Create and save a seller account in the database
        User seller = User.builder()
                .email(userEmail)
                .password(passwordEncoder.encode(userPassword))
                .firstName("Test")
                .lastName("Seller")
                .role(Role.SELLER)
                .isEmailConfirmed(true)
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(seller);
        // Step 2: Authenticate as a regular user and obtain JWT token
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTO.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        String response = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // Step 3: Attempt to access admin-only endpoints with a regular user token
        // Expect all responses to be HTTP 403 Forbidden
        mockMvc.perform(get("/admin/users/pending")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/users/1/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }



    /**
     * Tests successful user registration and subsequent confirmation.
     * Also verifies admin authentication and approval process.
     */
    @Test
    @DisplayName("Check registration with valid input (POST)")
    void testRegistrationEndpointSuccess() throws Exception {

        // Step 1: Register a new user
        RegistrationRequestDTO userRegistrationRequest =  RegistrationRequestDTO
                .builder()
                .firstName(userName)
                .lastName(userLastName)
                .password(userPassword)
                .email(userEmail)
                .build();

        String userResponseContent = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("sheinnickolas@gmail.com"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.status").value(Status.PENDING.getValueOfStatus()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode userResponseNode = objectMapper.readTree(userResponseContent);
        String userAuthToken = userResponseNode.get("token").asText();
        System.out.println("User token: " + userAuthToken);

        // Step 2: Confirm user registration
        mockMvc.perform(get("/auth/confirm")
                        .param("token", userAuthToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        String.format("Email is %s. Waiting for admin approval.",
                        Status.ACTIVE.getValueOfStatus())
                ));

        // Step 3: Admin logs in
        AuthenticationRequestDTO adminAuthRequest =  AuthenticationRequestDTO
                .builder()
                .email(adminEmail)
                .password(adminPassword)
                .build();

        String adminResponseContent = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminAuthRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.user").value(adminEmail))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.status").value(Status.AUTHENTICATED.getValueOfStatus()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode adminResponseNode = objectMapper.readTree(adminResponseContent);
        String adminAuthToken = adminResponseNode.get("token").asText();
        System.out.println("Admin token:" + adminAuthToken);

        // Step 4: Admin approves the user
        mockMvc.perform(get("/admin/confirm")
                        .header("Authorization", "Bearer " + adminAuthToken)
                        .param("email", userEmail)
                        .param("confirm", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content()
                        .string("User Active and Approved with pending comments processed"));
    }

    /**
     * Tests registration attempt with a duplicate email.
     * Expected result: Conflict status (409).
     */
    @Test
    @DisplayName("Check registration with duplicate email")
    void testDuplicateEmailRegistrationFail() throws Exception {
        RegistrationRequestDTO request = RegistrationRequestDTO.builder()
                .firstName(userName)
                .lastName(userLastName)
                .password(userPassword)
                .email(userEmail)
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    /**
     * Tests registration with invalid input data.
     * Expected result: Bad request status (400).
     */
    @Test
    @DisplayName("Check invalid registration data")
    void testInvalidRegistrationData() throws Exception {
        // Missing required fields
        RegistrationRequestDTO invalidRequest = RegistrationRequestDTO.builder()
                .firstName(userName)
                .email(userEmail)
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Invalid email format
        RegistrationRequestDTO invalidEmailRequest = RegistrationRequestDTO.builder()
                .firstName(userName)
                .lastName(userLastName)
                .password(userPassword)
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());
    }

}
