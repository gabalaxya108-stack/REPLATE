package com.replate.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.entity.BaseUser;
import com.replate.domain.entity.DonorProfile;
import com.replate.domain.enums.UserRole;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RegisterRequestDTO;
import com.replate.repository.DonorProfileRepository;
import com.replate.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-End Verification Test for Donor Registration, PostgreSQL Database Persistence,
 * BCrypt Password Hashing, Foreign Key Linkage, and Authenticated Session Retrieval.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DonorRegistrationPersistenceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("E2E: Register Commercial Donor -> Persist in app_user & donor_profile -> Verify BCrypt Hash -> Retrieve /auth/me")
    void testFullDonorRegistrationAndPersistenceLifecycle() throws Exception {
        // Step 1: Prepare registration request for an Indian commercial food establishment
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setFullName("The Rameshwaram Cafe Banquets");
        registerDTO.setEmail("contact@rameshwaramcafe.in");
        registerDTO.setPassword("Cafe@Strong123");
        registerDTO.setPhoneNumber("+91-9876543210");
        registerDTO.setRole(UserRole.DONOR);
        registerDTO.setAddressLine1("12th Main Road, HAL 2nd Stage, Indiranagar");
        registerDTO.setCity("Bengaluru");
        registerDTO.setState("Karnataka");
        registerDTO.setPincode("560038");
        registerDTO.setOrganizationName("The Rameshwaram Cafe Banquets Pvt Ltd");
        registerDTO.setOrganizationType("RESTAURANT");
        registerDTO.setFssaiLicenseNo("11220334000888");
        registerDTO.setOperatingHours("06:00 AM - 11:30 PM");

        // Step 2: Perform HTTP POST /api/v1/auth/register
        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("contact@rameshwaramcafe.in"))
                .andExpect(jsonPath("$.data.role").value("DONOR"))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andReturn();

        JsonNode registerResponseJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String jwtToken = registerResponseJson.path("data").path("accessToken").asText();
        Long registeredUserId = registerResponseJson.path("data").path("userId").asLong();

        assertNotNull(jwtToken, "Access token must not be null upon registration");
        assertTrue(registeredUserId > 0, "Registered user ID must be greater than 0");

        // Step 3: Verify Persistence in PostgreSQL app_user table via UserRepository
        Optional<BaseUser> persistedUserOpt = userRepository.findByEmail("contact@rameshwaramcafe.in");
        assertTrue(persistedUserOpt.isPresent(), "User must be found in database by email");

        BaseUser persistedUser = persistedUserOpt.get();
        assertEquals("The Rameshwaram Cafe Banquets", persistedUser.getFullName());
        assertEquals("contact@rameshwaramcafe.in", persistedUser.getEmail());
        assertEquals(UserRole.DONOR, persistedUser.getRole());
        assertEquals("ACTIVE", persistedUser.getStatus());
        assertEquals("+91-9876543210", persistedUser.getPhoneNumber());

        // Location Coordinate verification (Composition)
        assertNotNull(persistedUser.getLocation(), "Embedded location coordinate must not be null");
        assertEquals("12th Main Road, HAL 2nd Stage, Indiranagar", persistedUser.getLocation().getAddressLine1());
        assertEquals("Bengaluru", persistedUser.getLocation().getCity());
        assertEquals("Karnataka", persistedUser.getLocation().getState());
        assertEquals("560038", persistedUser.getLocation().getPincode());

        // BCrypt Password Security verification
        assertNotEquals("Cafe@Strong123", persistedUser.getPasswordHash(), "Plaintext password must NEVER be persisted");
        assertTrue(persistedUser.getPasswordHash().startsWith("$2a$") || persistedUser.getPasswordHash().startsWith("$2b$"),
                "Password must be hashed using standard BCrypt algorithm");
        assertTrue(passwordEncoder.matches("Cafe@Strong123", persistedUser.getPasswordHash()),
                "BCrypt encoder must successfully verify the original raw password against the stored hash");

        // Step 4: Verify Persistence in PostgreSQL donor_profile table via DonorProfileRepository
        Optional<DonorProfile> persistedProfileOpt = donorProfileRepository.findById(persistedUser.getId());
        assertTrue(persistedProfileOpt.isPresent(), "DonorProfile must be persisted and keyed by userId (@MapsId)");

        DonorProfile persistedProfile = persistedProfileOpt.get();
        assertEquals("The Rameshwaram Cafe Banquets Pvt Ltd", persistedProfile.getOrganizationName());
        assertEquals("RESTAURANT", persistedProfile.getOrganizationType());
        assertEquals("11220334000888", persistedProfile.getFssaiLicenseNo());
        assertEquals("06:00 AM - 11:30 PM", persistedProfile.getOperatingHours());
        assertEquals(persistedUser.getId(), persistedProfile.getUser().getId(),
                "Foreign key relationship between donor_profile and app_user must be consistent");

        // Step 5: Test Authenticated GET /api/v1/auth/me using the generated JWT Token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(persistedUser.getId()))
                .andExpect(jsonPath("$.data.email").value("contact@rameshwaramcafe.in"))
                .andExpect(jsonPath("$.data.role").value("DONOR"))
                .andExpect(jsonPath("$.data.addressLine1").value("12th Main Road, HAL 2nd Stage, Indiranagar"))
                .andExpect(jsonPath("$.data.profileDetails.organizationName").value("The Rameshwaram Cafe Banquets Pvt Ltd"))
                .andExpect(jsonPath("$.data.profileDetails.fssaiLicenseNo").value("11220334000888"))
                .andExpect(jsonPath("$.data.profileDetails.organizationType").value("RESTAURANT"));

        // Step 6: Test Login with newly registered credentials
        LoginRequestDTO loginDTO = new LoginRequestDTO("contact@rameshwaramcafe.in", "Cafe@Strong123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.userId").value(persistedUser.getId()))
                .andExpect(jsonPath("$.data.role").value("DONOR"));

        // Step 7: Test Login with invalid password
        LoginRequestDTO invalidLoginDTO = new LoginRequestDTO("contact@rameshwaramcafe.in", "WrongPassword!99");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        // Step 8: Test Duplicate Email Registration Protection
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }
}

