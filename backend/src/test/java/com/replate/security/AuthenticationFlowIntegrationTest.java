package com.replate.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.enums.UserRole;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RegisterRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    @DisplayName("1. Successful Donor Registration")
    void testSuccessfulDonorRegistration() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setFullName("Punjab Grill Banquets");
        dto.setEmail("contact@punjabgrill.in");
        dto.setPassword("Secret@12345");
        dto.setPhoneNumber("+91-9876543299");
        dto.setRole(UserRole.DONOR);
        dto.setAddressLine1("Ambience Mall, NH-8");
        dto.setCity("Gurugram");
        dto.setState("Haryana");
        dto.setPincode("122002");
        dto.setOrganizationName("Punjab Grill Hospitality Ltd");
        dto.setOrganizationType("RESTAURANT");
        dto.setFssaiLicenseNo("10819001000999");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("contact@punjabgrill.in"))
                .andExpect(jsonPath("$.data.role").value("DONOR"));
    }

    @Test
    @DisplayName("2. Duplicate Email Registration Rejection")
    void testDuplicateEmailRegistrationRejection() throws Exception {
        // 'contact@saravanabhavan.in' already exists in seed data
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setFullName("Duplicate Saravana Test");
        dto.setEmail("contact@saravanabhavan.in");
        dto.setPassword("Password@123");
        dto.setPhoneNumber("+91-9876543210");
        dto.setRole(UserRole.DONOR);
        dto.setAddressLine1("Test Road");
        dto.setCity("Bengaluru");
        dto.setState("Karnataka");
        dto.setPincode("560038");
        dto.setOrganizationName("Another Saravana");
        dto.setOrganizationType("RESTAURANT");
        dto.setFssaiLicenseNo("11220334999999");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An account with email 'contact@saravanabhavan.in' is already registered."));
    }

    @Test
    @DisplayName("3. Successful Login with Seeded Donor")
    void testSuccessfulLogin() throws Exception {
        LoginRequestDTO loginDto = new LoginRequestDTO("contact@saravanabhavan.in", "Password@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.role").value("DONOR"))
                .andExpect(jsonPath("$.data.email").value("contact@saravanabhavan.in"));
    }

    @Test
    @DisplayName("4. Invalid Password Rejection")
    void testInvalidPasswordRejection() throws Exception {
        LoginRequestDTO loginDto = new LoginRequestDTO("contact@saravanabhavan.in", "WrongPassword999");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email address or password."));
    }

    @Test
    @DisplayName("5. Unknown Email Rejection")
    void testUnknownEmailRejection() throws Exception {
        LoginRequestDTO loginDto = new LoginRequestDTO("nonexistent.user@random.org", "Password@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("6. JWT Generation & Validation")
    void testJwtGenerationAndValidation() {
        String token = tokenProvider.generateTokenFromUsername("test.user@replate.org", 999L, "VOLUNTEER");
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("test.user@replate.org", tokenProvider.getEmailFromJWT(token));
    }

    @Test
    @DisplayName("7. Expired JWT Rejection")
    void testExpiredJwtRejection() {
        // Instantiate a provider with 1 millisecond expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
                -1000L // Already expired 1 second ago
        );
        String expiredToken = shortLivedProvider.generateTokenFromUsername("expired@replate.org", 888L, "NGO");

        assertFalse(tokenProvider.validateToken(expiredToken), "Expired token must be rejected");
    }

    @Test
    @DisplayName("8. Missing Token Rejection on Protected Endpoint")
    void testMissingTokenRejection() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("9. Invalid / Malformed Token Rejection")
    void testInvalidTokenRejection() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer this.is.a.malformed.bogus.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("10. Unauthorized Role Access Rejection (DONOR blocked from ADMIN route)")
    void testUnauthorizedRoleAccess() throws Exception {
        // Authenticate as DONOR
        LoginRequestDTO loginDto = new LoginRequestDTO("contact@saravanabhavan.in", "Password@123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = root.path("data").path("accessToken").asText();

        // DONOR attempts to access ADMIN ping endpoint -> must be 403 Forbidden
        mockMvc.perform(get("/api/v1/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("11. ADMIN Registration Rejection Through Public Channels")
    void testAdminRegistrationRejection() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setFullName("Malicious Admin Attempt");
        dto.setEmail("hacker@privilege.escalation.org");
        dto.setPassword("Secret@123");
        dto.setPhoneNumber("+91-9999999999");
        dto.setRole(UserRole.ADMIN); // Attempt privilege escalation!
        dto.setAddressLine1("Dark Web Road");
        dto.setCity("Bengaluru");
        dto.setState("Karnataka");
        dto.setPincode("560001");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Registration with ADMIN role is strictly prohibited through public channels."));
    }

    @Test
    @DisplayName("12. Password Hash Not Exposed in API Responses")
    void testPasswordHashNotExposed() throws Exception {
        // Authenticate as Volunteer
        LoginRequestDTO loginDto = new LoginRequestDTO("aarav.sharma@gmail.com", "Password@123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        assertFalse(loginResponse.contains("passwordHash"), "Login response must NEVER expose passwordHash");
        assertFalse(loginResponse.contains("$2a$10$"), "Login response must NEVER expose BCrypt hashes");

        JsonNode root = objectMapper.readTree(loginResponse);
        String token = root.path("data").path("accessToken").asText();

        // Check /me response
        MvcResult meResult = mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String meResponse = meResult.getResponse().getContentAsString();
        assertFalse(meResponse.contains("passwordHash"), "/me profile response must NEVER expose passwordHash");
        assertFalse(meResponse.contains("$2a$10$"), "/me profile response must NEVER expose BCrypt hashes");
        assertFalse(meResponse.contains("Password@123"), "/me profile response must NEVER expose raw password");
    }
}
