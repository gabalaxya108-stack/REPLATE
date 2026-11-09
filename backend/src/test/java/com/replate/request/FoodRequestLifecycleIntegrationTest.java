package com.replate.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.entity.FoodDonation;
import com.replate.domain.entity.FoodRequest;
import com.replate.domain.enums.*;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.CreateFoodRequestDTO;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.dto.request.RejectFoodRequestDTO;
import com.replate.repository.FoodDonationRepository;
import com.replate.repository.FoodRequestRepository;
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
public class FoodRequestLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FoodRequestRepository foodRequestRepository;

    @Autowired
    private FoodDonationRepository foodDonationRepository;

    private String donorToken1;     // Saravana Bhavan (User 1)
    private String donorToken2;     // Haldiram's (User 2)
    private String ngoToken;        // Robin Hood Army (User 4)
    private String volunteerToken;  // Aarav Sharma (User 5)

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

        // Authenticate Volunteer
        MvcResult resVol = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("aarav.sharma@gmail.com", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        volunteerToken = objectMapper.readTree(resVol.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    }

    private Long createTestDonation(String token, String title, BigDecimal qty, boolean publish) throws Exception {
        CreateDonationRequestDTO dto = new CreateDonationRequestDTO(
                title,
                FoodCategory.COOKED_MEALS,
                qty,
                QuantityUnit.MEAL_BOXES,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                Instant.now().minus(30, ChronoUnit.MINUTES),
                Instant.now().plus(6, ChronoUnit.HOURS),
                "Kitchen pickup point at rear gate.",
                publish
        );

        MvcResult result = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("id").asLong();
    }

    @Test
    @DisplayName("1. Successful NGO Request Creation -> Verify Persistence & NO Stock Deduction")
    void testSuccessfulNgoRequestCreation() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Vegetable Biryani Pots", new BigDecimal("100.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(
                donationId,
                new BigDecimal("40.00"),
                "Distributing hot meals to 40 families at Indiranagar temporary shelter."
        );

        MvcResult result = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.requestedQuantity").value(40.0))
                .andReturn();

        Long requestId = objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Verify in database
        Optional<FoodRequest> dbRequest = foodRequestRepository.findById(requestId);
        assertTrue(dbRequest.isPresent(), "Request must be persisted in food_request table");
        assertEquals(RequestStatus.PENDING, dbRequest.get().getStatus());
        assertEquals(new BigDecimal("40.00"), dbRequest.get().getRequestedQuantity());

        // CRITICAL STEP 5 BOUNDARY: Request creation MUST NOT deduct stock from donation!
        FoodDonation donation = foodDonationRepository.findById(donationId).get();
        assertEquals(new BigDecimal("100.00"), donation.getAvailableQuantity(),
                "Available quantity must remain 100.00 (NO stock deduction upon request creation)");
        assertEquals(DonationStatus.AVAILABLE, donation.getStatus(),
                "Donation status must remain AVAILABLE");
    }

    @Test
    @DisplayName("2. Non-NGO User (Donor/Volunteer) Cannot Submit Request")
    void testNonNgoCannotSubmitRequest() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Sambar Rice Batch", new BigDecimal("50.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(
                donationId,
                new BigDecimal("20.00"),
                "Unauthorized attempt"
        );

        // Attempting as Volunteer -> 403 Forbidden
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + volunteerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        // Attempting as Donor -> 403 Forbidden
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + donorToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("3. Requesting Non-Existent Donation Returns 404")
    void testRequestNonExistentDonation() throws Exception {
        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(
                999999L,
                new BigDecimal("10.00"),
                "Notes"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("4. Requesting DRAFT (Unpublished) Donation is Rejected")
    void testRequestDraftDonationRejection() throws Exception {
        // Create as DRAFT (publishImmediately = false)
        Long draftDonationId = createTestDonation(donorToken1, "Private Kitchen Experiment", new BigDecimal("30.00"), false);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(
                draftDonationId,
                new BigDecimal("10.00"),
                "Attempt to claim draft"
        );

        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("5. Zero, Negative, or Excessive Quantity Rejection")
    void testInvalidQuantityRejection() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Idli Batch Limited", new BigDecimal("25.00"), true);

        // 1. Zero quantity
        CreateFoodRequestDTO zeroReq = new CreateFoodRequestDTO(donationId, BigDecimal.ZERO, "Zero test");
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroReq)))
                .andExpect(status().isBadRequest());

        // 2. Excessive quantity (> 25.00 available)
        CreateFoodRequestDTO excessiveReq = new CreateFoodRequestDTO(donationId, new BigDecimal("30.00"), "Excessive test");
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessiveReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("exceeds currently available")));
    }

    @Test
    @DisplayName("6. Duplicate Pending Request Rejection")
    void testDuplicatePendingRequestRejection() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Curd Rice Pots", new BigDecimal("60.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(
                donationId,
                new BigDecimal("20.00"),
                "First legitimate request"
        );

        // First request succeeds
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        // Second request for the same donation by the same NGO while first is PENDING is rejected
        mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already have a pending request")));
    }

    @Test
    @DisplayName("7. Donor Approves Request -> Status is APPROVED & NO Stock Deduction")
    void testDonorApprovesRequest() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Paneer Pulao", new BigDecimal("50.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("30.00"), "Meal distribution");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Unauthorized Donor 2 attempts to approve Donor 1's request -> 403 Forbidden
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/approve")
                        .header("Authorization", "Bearer " + donorToken2))
                .andExpect(status().isForbidden());

        // Legitimate Donor 1 approves the request
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/approve")
                        .header("Authorization", "Bearer " + donorToken1)
                        .param("remarks", "Approved for immediate distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // Direct DB verification of request status
        FoodRequest dbRequest = foodRequestRepository.findById(requestId).get();
        assertEquals(RequestStatus.APPROVED, dbRequest.getStatus());

        // CRITICAL STEP 5 BOUNDARY: Request approval does NOT deduct stock!
        FoodDonation dbDonation = foodDonationRepository.findById(donationId).get();
        assertEquals(new BigDecimal("50.00"), dbDonation.getAvailableQuantity(),
                "Stock remains 50.00 until the Step 7 Allocation engine explicitly executes atomic allocation");
    }

    @Test
    @DisplayName("8. Donor Rejects Request with Justification")
    void testDonorRejectsRequest() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Tomato Rice", new BigDecimal("40.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("20.00"), "Distribution notes");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        RejectFoodRequestDTO rejectDTO = new RejectFoodRequestDTO("Food reserved for nearby community orphanage.");

        // Donor 1 rejects
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/reject")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.rejectionReason").value("Food reserved for nearby community orphanage."));

        // Cannot approve/reject an already processed request
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/approve")
                        .header("Authorization", "Bearer " + donorToken1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already been processed")));
    }

    @Test
    @DisplayName("9. NGO Withdraws Pending Request")
    void testNgoWithdrawsRequest() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Dal Tadka & Rotis", new BigDecimal("70.00"), true);

        CreateFoodRequestDTO requestDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("25.00"), "Notes");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Withdraw
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/withdraw")
                        .header("Authorization", "Bearer " + ngoToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("WITHDRAWN"));

        // Direct DB verification
        FoodRequest dbRequest = foodRequestRepository.findById(requestId).get();
        assertEquals(RequestStatus.WITHDRAWN, dbRequest.getStatus());
    }

    @Test
    @DisplayName("10. View My Requests (NGO) and View Received Requests (Donor) Paginated")
    void testViewRequestsPaginated() throws Exception {
        // NGO views submitted requests
        mockMvc.perform(get("/api/v1/requests/mine")
                        .header("Authorization", "Bearer " + ngoToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        // Donor views received requests
        mockMvc.perform(get("/api/v1/requests/received")
                        .header("Authorization", "Bearer " + donorToken1)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
