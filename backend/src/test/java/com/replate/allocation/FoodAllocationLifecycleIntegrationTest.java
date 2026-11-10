package com.replate.allocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.entity.Allocation;
import com.replate.domain.entity.FoodDonation;
import com.replate.domain.entity.FoodRequest;
import com.replate.domain.enums.*;
import com.replate.dto.request.CreateAllocationDTO;
import com.replate.dto.request.CreateDonationRequestDTO;
import com.replate.dto.request.CreateFoodRequestDTO;
import com.replate.dto.request.LoginRequestDTO;
import com.replate.repository.AllocationRepository;
import com.replate.repository.FoodDonationRepository;
import com.replate.repository.FoodRequestRepository;
import com.replate.service.allocation.AllocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FoodAllocationLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private FoodDonationRepository foodDonationRepository;

    @Autowired
    private FoodRequestRepository foodRequestRepository;

    @Autowired
    private AllocationService allocationService;

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
        Instant now = Instant.now();
        CreateDonationRequestDTO createDTO = new CreateDonationRequestDTO(
                title,
                FoodCategory.COOKED_MEALS,
                qty,
                QuantityUnit.MEAL_BOXES,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                now.minus(1, ChronoUnit.HOURS),
                now.plus(6, ChronoUnit.HOURS),
                "Allocation test kitchen batch",
                publish
        );

        MvcResult result = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("id").asLong();
    }

    private Long createAndApproveRequest(Long donationId, BigDecimal requestedQty) throws Exception {
        CreateFoodRequestDTO reqDTO = new CreateFoodRequestDTO(donationId, requestedQty, "Food allocation integration test justification");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Donor approves request
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/approve")
                        .header("Authorization", "Bearer " + donorToken1))
                .andExpect(status().isOk());

        return requestId;
    }

    @Test
    @DisplayName("1. Successful Full Allocation: Approved request receives full quantity and stock deducts")
    void testSuccessfulFullAllocation() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Full Allocation Meal Batch", new BigDecimal("50.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("50.00"));

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("50.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.allocatedQuantity").value(50.00))
                .andExpect(jsonPath("$.data.status").value("AWAITING_PICKUP"))
                .andExpect(jsonPath("$.data.donationAvailableQuantity").value(0.00));

        // Verify entity state
        FoodDonation updatedDonation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(updatedDonation.getAvailableQuantity()).isEqualByComparingTo("0.00");
        assertThat(updatedDonation.getStatus()).isEqualTo(DonationStatus.FULLY_ALLOCATED);
    }

    @Test
    @DisplayName("2. Successful Partial Allocation: Allocating portion leaves remainder in both donation & request")
    void testSuccessfulPartialAllocation() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Partial Allocation Batch", new BigDecimal("80.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("60.00"));

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("35.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.allocatedQuantity").value(35.00))
                .andExpect(jsonPath("$.data.donationAvailableQuantity").value(45.00));

        FoodDonation updatedDonation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(updatedDonation.getAvailableQuantity()).isEqualByComparingTo("45.00");
        assertThat(updatedDonation.getStatus()).isEqualTo(DonationStatus.PARTIALLY_ALLOCATED);
    }

    @Test
    @DisplayName("3. Allocation against PENDING request is rejected")
    void testAllocationAgainstPendingRequestRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Pending Request Batch", new BigDecimal("40.00"), true);

        // Submit request but DO NOT approve it
        CreateFoodRequestDTO reqDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("20.00"), "Unapproved claim");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Only APPROVED food requests can receive inventory allocation")));
    }

    @Test
    @DisplayName("4. Allocation against REJECTED request is rejected")
    void testAllocationAgainstRejectedRequestRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Rejected Claim Batch", new BigDecimal("40.00"), true);

        CreateFoodRequestDTO reqDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("20.00"), "Claim to be declined");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Decline request
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/reject")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rejectionReason\":\"Cannot fulfill at this time.\"}"))
                .andExpect(status().isOk());

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Only APPROVED food requests can receive inventory allocation")));
    }

    @Test
    @DisplayName("5. Allocation against WITHDRAWN request is rejected")
    void testAllocationAgainstWithdrawnRequestRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Withdrawn Claim Batch", new BigDecimal("40.00"), true);

        CreateFoodRequestDTO reqDTO = new CreateFoodRequestDTO(donationId, new BigDecimal("20.00"), "Claim to withdraw");
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Withdraw request as NGO
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/withdraw")
                        .header("Authorization", "Bearer " + ngoToken))
                .andExpect(status().isOk());

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Only APPROVED food requests can receive inventory allocation")));
    }

    @Test
    @DisplayName("6. Allocation against expired or unavailable donation is rejected")
    void testAllocationAgainstUnavailableDonationRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Available Then Cancelled Batch", new BigDecimal("30.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("20.00"));

        // Cancel donation
        mockMvc.perform(post("/api/v1/donations/" + donationId + "/cancel")
                        .header("Authorization", "Bearer " + donorToken1))
                .andExpect(status().isOk());

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Food donation cannot receive allocations in current status: CANCELLED")));
    }

    @Test
    @DisplayName("7. Zero and negative quantities are rejected")
    void testZeroAndNegativeQuantityRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Zero Quantity Batch", new BigDecimal("30.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("20.00"));

        // Zero
        CreateAllocationDTO zeroDTO = new CreateAllocationDTO(requestId, BigDecimal.ZERO);
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zeroDTO)))
                .andExpect(status().isBadRequest());

        // Negative
        CreateAllocationDTO negativeDTO = new CreateAllocationDTO(requestId, new BigDecimal("-10.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("8. Allocation exceeding approved request quantity is rejected")
    void testAllocationExceedingApprovedRequestQuantityRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Excess Request Batch", new BigDecimal("100.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("20.00"));

        // Request was only for 20.00, donor attempts to allocate 25.00
        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("25.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("exceeds the request's remaining unallocated balance")));
    }

    @Test
    @DisplayName("9. Allocation exceeding available donation stock is rejected")
    void testAllocationExceedingAvailableStockRejected() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Tight Stock Batch", new BigDecimal("15.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("15.00"));

        // Artificially request 20.00 (> 15.00 available)
        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("20.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("exceeds")));
    }

    @Test
    @DisplayName("10. Multiple allocations cannot exceed the approved request quantity")
    void testMultipleAllocationsCannotExceedRequestQuantity() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Multi Allocation Batch", new BigDecimal("100.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("40.00"));

        // First allocation: 25.00 (Leaves 15.00 on request)
        CreateAllocationDTO alloc1 = new CreateAllocationDTO(requestId, new BigDecimal("25.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alloc1)))
                .andExpect(status().isCreated());

        // Second allocation: 15.00 (Leaves 0.00 on request)
        CreateAllocationDTO alloc2 = new CreateAllocationDTO(requestId, new BigDecimal("15.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alloc2)))
                .andExpect(status().isCreated());

        // Third allocation: 1.00 (Should be rejected since request is fully fulfilled)
        CreateAllocationDTO alloc3 = new CreateAllocationDTO(requestId, new BigDecimal("1.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alloc3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already been fully allocated")));
    }

    @Test
    @DisplayName("11. Multiple NGO requests cannot collectively consume more than donation inventory")
    void testMultipleNgoRequestsCannotExceedInventory() throws Exception {
        // Initial stock: 40 boxes
        Long donationId = createTestDonation(donorToken1, "Limited Inventory Batch", new BigDecimal("40.00"), true);

        // NGO request for 30 boxes approved
        Long reqId1 = createAndApproveRequest(donationId, new BigDecimal("30.00"));

        // Allocate 30 boxes to request 1 -> Leaves 10 boxes available
        CreateAllocationDTO alloc1 = new CreateAllocationDTO(reqId1, new BigDecimal("30.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alloc1)))
                .andExpect(status().isCreated());

        // Attempting to allocate another 15 boxes against request 1 or another claim will fail (> 10 available)
        FoodDonation currentDonation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(currentDonation.getAvailableQuantity()).isEqualByComparingTo("10.00");
    }

    @Test
    @DisplayName("12. Unauthorized donor cannot allocate another donor's donation")
    void testUnauthorizedDonorCannotAllocate() throws Exception {
        // Created by Donor 1 (Saravana Bhavan)
        Long donationId = createTestDonation(donorToken1, "Donor 1 Owned Batch", new BigDecimal("50.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("25.00"));

        // Donor 2 (Haldiram's) attempts to allocate Donor 1's donation
        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("25.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("13. NGO cannot initiate donor-only allocation operations")
    void testNgoCannotInitiateAllocation() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Privilege Gate Batch", new BigDecimal("50.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("25.00"));

        // NGO attempts to execute allocation
        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("25.00"));

        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("14. Donation status changes to PARTIALLY_ALLOCATED when stock remains")
    void testDonationStatusPartiallyAllocated() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Partial Status Transition Batch", new BigDecimal("100.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("40.00"));

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("40.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isCreated());

        FoodDonation donation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(donation.getStatus()).isEqualTo(DonationStatus.PARTIALLY_ALLOCATED);
        assertThat(donation.getAvailableQuantity()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("15. Donation status changes to FULLY_ALLOCATED when available stock reaches 0")
    void testDonationStatusFullyAllocated() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Full Status Transition Batch", new BigDecimal("50.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("50.00"));

        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, new BigDecimal("50.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isCreated());

        FoodDonation donation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(donation.getStatus()).isEqualTo(DonationStatus.FULLY_ALLOCATED);
        assertThat(donation.getAvailableQuantity()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("16. Failed allocation leaves both stock and allocation records unchanged")
    void testFailedAllocationRollbackIntegrity() throws Exception {
        Long donationId = createTestDonation(donorToken1, "Rollback Verification Batch", new BigDecimal("30.00"), true);
        Long requestId = createAndApproveRequest(donationId, new BigDecimal("20.00"));

        long initialAllocationCount = allocationRepository.count();

        // Attempt invalid allocation: 35.00 (> 30.00 available)
        CreateAllocationDTO invalidAlloc = new CreateAllocationDTO(requestId, new BigDecimal("35.00"));
        mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAlloc)))
                .andExpect(status().isBadRequest());

        // Verify inventory and allocation count are strictly untouched
        FoodDonation donation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(donation.getAvailableQuantity()).isEqualByComparingTo("30.00");
        assertThat(donation.getStatus()).isEqualTo(DonationStatus.AVAILABLE);
        assertThat(allocationRepository.count()).isEqualTo(initialAllocationCount);
    }

    @Test
    @DisplayName("17. Concurrency Test: Concurrent allocation attempts cannot oversell inventory")
    void testConcurrentAllocationsCannotOversellInventory() throws Exception {
        // Initial stock: exactly 50.00 boxes
        final BigDecimal initialStock = new BigDecimal("50.00");
        Long donationId = createTestDonation(donorToken1, "Concurrent Allocation Test Batch", initialStock, true);

        // Create an approved request for 50.00 boxes
        Long requestId = createAndApproveRequest(donationId, initialStock);

        int numberOfThreads = 5;
        BigDecimal threadAllocQty = new BigDecimal("20.00"); // 5 * 20 = 100 > 50

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // Synchronize all threads to fire simultaneously

                    CreateAllocationDTO dto = new CreateAllocationDTO(requestId, threadAllocQty);
                    allocationService.allocateFood(dto, 1L, "DONOR");
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            }));
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown(); // Fire!

        for (Future<?> f : futures) {
            f.get(10, TimeUnit.SECONDS);
        }
        executor.shutdown();

        // Stock was 50. Each successful thread takes 20.
        // At most 2 threads can succeed (2 * 20 = 40 <= 50).
        // 3rd thread would need 20, but only 10 is left -> must fail!
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failureCount.get()).isEqualTo(3);

        FoodDonation finalDonation = foodDonationRepository.findById(donationId).orElseThrow();
        assertThat(finalDonation.getAvailableQuantity()).isEqualByComparingTo("10.00");
        assertThat(finalDonation.getStatus()).isEqualTo(DonationStatus.PARTIALLY_ALLOCATED);

        BigDecimal totalAllocatedInDb = allocationRepository.sumAllocatedQuantityByDonationId(donationId);
        assertThat(totalAllocatedInDb).isEqualByComparingTo("40.00");
    }
}
