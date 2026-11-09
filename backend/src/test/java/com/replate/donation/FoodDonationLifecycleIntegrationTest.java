package com.replate.donation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.entity.FoodDonation;
import com.replate.domain.enums.*;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.UpdateDonationRequestDTO;
import com.replate.repository.FoodDonationRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FoodDonationLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FoodDonationRepository foodDonationRepository;

    private String donorToken1; // Saravana Bhavan (User 1)
    private String donorToken2; // Haldiram's (User 2)
    private String ngoToken;    // Robin Hood Army (User 4)

    @BeforeEach
    void setUpTokens() throws Exception {
        // Authenticate Donor 1
        MvcResult res1 = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("contact@saravanabhavan.in", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        donorToken1 = objectMapper.readTree(res1.getResponse().getContentAsString()).path("data").path("accessToken").asText();

        // Authenticate Donor 2
        MvcResult res2 = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("events@haldiramsdelhi.com", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        donorToken2 = objectMapper.readTree(res2.getResponse().getContentAsString()).path("data").path("accessToken").asText();

        // Authenticate NGO
        MvcResult resNgo = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("foodrescue@robinhoodarmy.org", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        ngoToken = objectMapper.readTree(resNgo.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    }

    @Test
    @DisplayName("1. Create Draft Food Donation Listing -> Verify Persistence in PostgreSQL")
    void testCreateDraftDonation() throws Exception {
        Instant prepTime = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant expiryTime = Instant.now().plus(4, ChronoUnit.HOURS);

        CreateDonationRequestDTO request = new CreateDonationRequestDTO(
                "Fresh Idlis and Sambar Combo",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("40.00"),
                QuantityUnit.PORTIONS,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                prepTime,
                expiryTime,
                "Packed in insulated food containers at kitchen pickup point.",
                false // Save as draft
        );

        MvcResult result = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.totalQuantity").value(40.0))
                .andExpect(jsonPath("$.data.availableQuantity").value(40.0))
                .andReturn();

        Long donationId = objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Direct DB verification
        Optional<FoodDonation> dbDonation = foodDonationRepository.findById(donationId);
        assertTrue(dbDonation.isPresent(), "Donation must be persisted in food_donation table");
        assertEquals("Fresh Idlis and Sambar Combo", dbDonation.get().getTitle());
        assertEquals(DonationStatus.DRAFT, dbDonation.get().getStatus());
        assertEquals(new BigDecimal("40.00"), dbDonation.get().getTotalQuantity());
    }

    @Test
    @DisplayName("2. Create and Publish Immediately -> Verify State is AVAILABLE")
    void testCreateAndPublishImmediately() throws Exception {
        Instant prepTime = Instant.now().minus(30, ChronoUnit.MINUTES);
        Instant expiryTime = Instant.now().plus(6, ChronoUnit.HOURS);

        CreateDonationRequestDTO request = new CreateDonationRequestDTO(
                "Mini Meals Rice and Dal Boxes",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("60.00"),
                QuantityUnit.MEAL_BOXES,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                prepTime,
                expiryTime,
                "Hot box containers ready for immediate pickup.",
                true // Publish immediately
        );

        mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data.totalQuantity").value(60.0));
    }

    @Test
    @DisplayName("3. Zero or Negative Quantity Rejection")
    void testZeroQuantityRejection() throws Exception {
        CreateDonationRequestDTO request = new CreateDonationRequestDTO(
                "Invalid Zero Quantity Item",
                FoodCategory.COOKED_MEALS,
                BigDecimal.ZERO,
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.AMBIENT,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(2, ChronoUnit.HOURS),
                "Invalid",
                false
        );

        mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("4. Expiry Before Preparation Time Rejection")
    void testExpiryBeforePrepTimeRejection() throws Exception {
        Instant prepTime = Instant.now();
        Instant invalidExpiry = Instant.now().minus(2, ChronoUnit.HOURS); // Before prep time

        CreateDonationRequestDTO request = new CreateDonationRequestDTO(
                "Invalid Chronology Item",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("20.00"),
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.AMBIENT,
                prepTime,
                invalidExpiry,
                "Invalid",
                false
        );

        mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("5. Publish Draft -> Transitions to AVAILABLE")
    void testPublishDraftDonation() throws Exception {
        // Step 1: Create draft
        CreateDonationRequestDTO createReq = new CreateDonationRequestDTO(
                "Breads & Chapatis Batch",
                FoodCategory.BREADS_ROTIS,
                new BigDecimal("80.00"),
                QuantityUnit.PORTIONS,
                DietaryType.PURE_VEG,
                StorageCondition.AMBIENT,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(5, ChronoUnit.HOURS),
                "Freshly baked rotis wrapped in silver foil.",
                false
        );

        MvcResult createRes = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Step 2: Publish draft
        mockMvc.perform(post("/api/v1/donations/" + id + "/publish")
                        .header("Authorization", "Bearer " + donorToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));

        // Verify in DB
        assertEquals(DonationStatus.AVAILABLE, foodDonationRepository.findById(id).get().getStatus());
    }

    @Test
    @DisplayName("6. Edit Another Donor's Listing Rejection (Ownership Isolation)")
    void testEditOtherDonorListingRejection() throws Exception {
        // Donor 1 creates a listing
        CreateDonationRequestDTO createReq = new CreateDonationRequestDTO(
                "Saravana Special Masala Dosa Batter",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("25.00"),
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.REFRIGERATED,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(8, ChronoUnit.HOURS),
                "Notes",
                false
        );

        MvcResult res = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(res.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Donor 2 attempts to modify Donor 1's listing
        UpdateDonationRequestDTO updateReq = new UpdateDonationRequestDTO(
                "Hacked Title by Another Donor",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("50.00"),
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.REFRIGERATED,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(8, ChronoUnit.HOURS),
                "Hacked notes"
        );

        mockMvc.perform(put("/api/v1/donations/" + id)
                        .header("Authorization", "Bearer " + donorToken2) // Unauthorized donor
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("7. Cancel Active Donation -> Transitions to CANCELLED")
    void testCancelActiveDonation() throws Exception {
        CreateDonationRequestDTO createReq = new CreateDonationRequestDTO(
                "Temporary Surplus Sweets",
                FoodCategory.DAIRY_SWEETS,
                new BigDecimal("15.00"),
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.REFRIGERATED,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(6, ChronoUnit.HOURS),
                "Notes",
                true
        );

        MvcResult res = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(res.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Cancel
        mockMvc.perform(post("/api/v1/donations/" + id + "/cancel")
                        .header("Authorization", "Bearer " + donorToken1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        // Cannot edit a cancelled donation
        UpdateDonationRequestDTO updateReq = new UpdateDonationRequestDTO(
                "Attempt to edit cancelled",
                FoodCategory.DAIRY_SWEETS,
                new BigDecimal("15.00"),
                QuantityUnit.KG,
                DietaryType.PURE_VEG,
                StorageCondition.REFRIGERATED,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(6, ChronoUnit.HOURS),
                "Notes"
        );

        mockMvc.perform(put("/api/v1/donations/" + id)
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("8. Draft Privacy: Public Discovery does NOT expose DRAFT listings")
    void testDraftPrivacyInPublicDiscovery() throws Exception {
        // Create draft
        CreateDonationRequestDTO draftReq = new CreateDonationRequestDTO(
                "Secret Draft Dish",
                FoodCategory.COOKED_MEALS,
                new BigDecimal("30.00"),
                QuantityUnit.PORTIONS,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                Instant.now().minus(1, ChronoUnit.HOURS),
                Instant.now().plus(5, ChronoUnit.HOURS),
                "Confidential",
                false // DRAFT
        );

        MvcResult draftRes = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(draftReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long draftId = objectMapper.readTree(draftRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Public discovery search query
        MvcResult searchRes = mockMvc.perform(get("/api/v1/donations/public")
                        .param("query", "Secret Draft Dish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        JsonNode publicList = objectMapper.readTree(searchRes.getResponse().getContentAsString()).path("data").path("content");
        assertEquals(0, publicList.size(), "Draft donations must NEVER appear in public discovery search");

        // Unauthenticated direct access to draft by ID must be rejected
        mockMvc.perform(get("/api/v1/donations/public/" + draftId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("9. Get My Listings -> Paginated for Authenticated Donor")
    void testGetMyDonationsPaginated() throws Exception {
        mockMvc.perform(get("/api/v1/donations/mine")
                        .header("Authorization", "Bearer " + donorToken1)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0));
    }
}
