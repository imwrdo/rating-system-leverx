package org.leverx.ratingapp.comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.enums.Role;
import org.leverx.ratingapp.enums.Status;
import org.leverx.ratingapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Comment API endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Integration tests for Comment API endpoints")
@Tag("integration")
public class CommentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${admin.email}") private String adminEmail;
    @Value("${admin.password}") private String adminPassword;
    @Value("firstseller@test.com") private String userEmail;
    @Value("secondseller@test.com") private String sellerEmail;
    @Value("password123") private String userPassword;

    private String userToken;
    private String adminToken;
    private Long sellerId;

    /**
     * Sets up the necessary test data before each test:
     * - Creates and saves two seller users.
     * - Retrieves authentication tokens for the user and admin.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Create and save a seller account in the database
        User seller = User.builder()
                .email(sellerEmail)
                .password(passwordEncoder.encode(userPassword))
                .firstName("Test")
                .lastName("Seller")
                .role(Role.SELLER)
                .isEmailConfirmed(true)
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .build();
        sellerId = userRepository.save(seller).getId();

        // Create and save a second seller account in the database
        User user = User.builder()
                .email(userEmail)
                .password(passwordEncoder.encode(userPassword))
                .firstName("Test")
                .lastName("User")
                .role(Role.SELLER)
                .isEmailConfirmed(true)
                .isActivated(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        // Authenticate both user and admin to obtain JWT tokens
        userToken = getAuthToken(userEmail, userPassword);
        adminToken = getAuthToken(adminEmail, adminPassword);
    }

    /**
     * Tests comment creation and approval process.
     * Expected result:
     * - Comment is created with status "CREATED".
     * - Admin approves it, changing status to "APPROVED".
     * - The comment appears in the seller's comments list.
     */
    @Test
    @DisplayName("Create and approve comment flow")
    void testCreateAndApproveComment() throws Exception {
        // Step 1. Create comment request
        CommentRequestDTO commentRequest = new CommentRequestDTO("Great seller!", 5);

        // Step 2. Submit the comment request and verify it is created
        String commentResponse = mockMvc.perform(post("/users/{sellerId}/comments", sellerId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Great seller!"))
                .andExpect(jsonPath("$.grade").value(5))
                .andExpect(jsonPath("$.status")
                        .value(String
                                .format("Comment is %s, please wait for verification",
                                Status.CREATED.getValueOfStatus())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long commentId = objectMapper.readTree(commentResponse).get("id").asLong();

        // Step 3. Admin approves the comment
        mockMvc.perform(post("/admin/users/{sellerId}/comments/{commentId}", sellerId, commentId)
                .header("Authorization", "Bearer " + adminToken)
                .param("confirm", "true"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value(Status.APPROVED.getValueOfStatus()));

        // Step 4. Verify the comment appears in the seller's approved comments
        mockMvc.perform(get("/users/{sellerId}/comments", sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Great seller!"))
                .andExpect(jsonPath("$[0].grade").value(5))
                .andExpect(jsonPath("$[0].status").value(Status.APPROVED.getValueOfStatus()));
    }
    /**
     * Test the ability to update a comment.
     * This test verifies that an existing comment can be updated by the user.
     * Expected result: 200 (OK) status
     */
    @Test
    @DisplayName("Update comment")
    void testUpdateComment() throws Exception {
        // Step 1. Create initial comment
        CommentRequestDTO initialComment = new CommentRequestDTO("Initial review", 4);
        String createResponse = mockMvc.perform(post("/users/{sellerId}/comments", sellerId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialComment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long commentId = objectMapper.readTree(createResponse).get("id").asLong();

        // Step 2. Update the comment
        CommentRequestDTO updateRequest = new CommentRequestDTO("Updated review", 5);
        mockMvc.perform(put("/users/{sellerId}/comments/{commentId}", sellerId, commentId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated review"))
                .andExpect(jsonPath("$.grade").value(5))
                .andExpect(jsonPath("$.status").value(Status.UPDATED.getValueOfStatus()));
    }

    /**
     * Test the ability to delete a comment.
     * This test verifies that an existing comment can be deleted by the user.
     * Expected result: 410 (Deleted) and 404 (Not found)
     */
    @Test
    @DisplayName("Delete comment")
    void testDeleteComment() throws Exception {
        // Step 1. Create a comment
        CommentRequestDTO comment = new CommentRequestDTO("To be deleted", 3);
        String createResponse = mockMvc.perform(post("/users/{sellerId}/comments", sellerId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long commentId = objectMapper.readTree(createResponse).get("id").asLong();

        // Step 2. Delete the comment
        mockMvc.perform(delete("/users/{sellerId}/comments/{commentId}", sellerId, commentId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isAccepted())
                .andExpect(content().string(String.format("Comment %d is %s", 
                        commentId, Status.DELETED.getValueOfStatus())));

        // Step 3. Verify the comment is deleted
        mockMvc.perform(get("/users/{sellerId}/comments/{commentId}", sellerId, commentId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    /**
     * Helper method to authenticate and retrieve JWT token.
     * @param email - user email
     * @param password - user password
     * @return - JWT token value
     * @throws Exception - thrown if the authentication request fails
     */
    private String getAuthToken(String email, String password) throws Exception {
        AuthenticationRequestDTO authRequest = AuthenticationRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        String response = mockMvc.perform(post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode responseNode = objectMapper.readTree(response);
        return responseNode.get("token").asText();
    }
}
