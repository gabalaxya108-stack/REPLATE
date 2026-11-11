package com.replate.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replate.domain.entity.*;
import com.replate.domain.enums.*;
import com.replate.dto.request.*;
import com.replate.repository.*;
import com.replate.service.allocation.AllocationService;
import com.replate.service.delivery.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeliveryLifecycleIntegrationTest {

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
    private DeliveryTaskRepository deliveryTaskRepository;

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String donorToken1;     // Saravana Bhavan (User 1)
    private String donorToken2;     // Haldiram's (User 2)
    private String ngoToken;        // Robin Hood Army (User 4)
    private String volunteer1Token; // Aarav Sharma (User 6)
    private String volunteer2Token; // Pooja Deshmukh (User 7)

    private Long inactiveVolunteerUserId;

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

        // Authenticate Volunteer 1 (Aarav Sharma, user 6)
        MvcResult resVol1 = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("aarav.sharma@gmail.com", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        volunteer1Token = objectMapper.readTree(resVol1.getResponse().getContentAsString()).path("data").path("accessToken").asText();

        // Authenticate Volunteer 2 (Pooja Deshmukh, user 7)
        MvcResult resVol2 = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("pooja.deshmukh@gmail.com", "Password@123"))))
                .andExpect(status().isOk())
                .andReturn();
        volunteer2Token = objectMapper.readTree(resVol2.getResponse().getContentAsString()).path("data").path("accessToken").asText();

        // Ensure an inactive/suspended volunteer exists for negative tests
        BaseUser inactiveUser = userRepository.findByEmail("suspended.volunteer@replate.org").orElse(null);
        if (inactiveUser != null) {
            inactiveVolunteerUserId = inactiveUser.getId();
        } else {
            inactiveVolunteerUserId = 29L;
        }
    }

    private Long createFreshAllocation(String donorToken, BigDecimal qty) throws Exception {
        // 1. Create donation
        Instant now = Instant.now();
        CreateDonationRequestDTO donationDTO = new CreateDonationRequestDTO(
                "Delivery Batch " + System.currentTimeMillis(),
                FoodCategory.COOKED_MEALS,
                qty,
                QuantityUnit.MEAL_BOXES,
                DietaryType.PURE_VEG,
                StorageCondition.HOT_INSULATED,
                now.minus(1, ChronoUnit.HOURS),
                now.plus(8, ChronoUnit.HOURS),
                "Hot dispatch kitchen batch",
                true
        );
        MvcResult donResult = mockMvc.perform(post("/api/v1/donations")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        Long donationId = objectMapper.readTree(donResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // 2. Create NGO request
        CreateFoodRequestDTO reqDTO = new CreateFoodRequestDTO(
                donationId,
                qty,
                "Urgent delivery requirement for evening soup kitchen"
        );
        MvcResult reqResult = mockMvc.perform(post("/api/v1/requests")
                        .header("Authorization", "Bearer " + ngoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        Long requestId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).path("data").path("id").asLong();

        // 3. Donor approves request
        mockMvc.perform(post("/api/v1/requests/" + requestId + "/approve")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Create allocation
        CreateAllocationDTO allocDTO = new CreateAllocationDTO(requestId, qty);
        MvcResult allocResult = mockMvc.perform(post("/api/v1/allocations")
                        .header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allocDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(allocResult.getResponse().getContentAsString()).path("data").path("id").asLong();
    }

    @Test
    @DisplayName("01. Create delivery task for confirmed allocation")
    void test01_createDeliveryTaskForConfirmedAllocation() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("30.00"));

        mockMvc.perform(post("/api/v1/deliveries/allocation/" + allocationId + "/dispatch")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.allocationId", is(allocationId.intValue())))
                .andExpect(jsonPath("$.data.status", is("UNASSIGNED")))
                .andExpect(jsonPath("$.data.pickupOtp", notNullValue()))
                .andExpect(jsonPath("$.data.deliveryOtp", notNullValue()))
                .andExpect(jsonPath("$.data.progressPercentage", is(10)));
    }

    @Test
    @DisplayName("02. Reject delivery creation for unconfirmed/cancelled allocation")
    void test02_rejectDeliveryCreationForUnconfirmedAllocation() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("20.00"));

        // Cancel allocation in DB
        Allocation alloc = allocationRepository.findById(allocationId).orElseThrow();
        alloc.setStatus(AllocationStatus.CANCELLED);
        allocationRepository.save(alloc);

        mockMvc.perform(post("/api/v1/deliveries/allocation/" + allocationId + "/dispatch")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Cannot create delivery task for an unconfirmed or cancelled allocation")));
    }

    @Test
    @DisplayName("03. Prevent duplicate active delivery tasks for an allocation")
    void test03_preventDuplicateActiveDeliveryTasksForAllocation() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("25.00"));

        // First dispatch succeeds
        mockMvc.perform(post("/api/v1/deliveries/allocation/" + allocationId + "/dispatch")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Second dispatch for same allocation must be rejected
        mockMvc.perform(post("/api/v1/deliveries/allocation/" + allocationId + "/dispatch")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("An active delivery task already exists")));
    }

    @Test
    @DisplayName("04. Successfully assign an eligible volunteer")
    void test04_successfullyAssignEligibleVolunteer() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("40.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Handle with extreme care, insulated bags required");

        mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("ASSIGNED")))
                .andExpect(jsonPath("$.data.volunteer.userId", is(6)))
                .andExpect(jsonPath("$.data.volunteer.fullName", is("Aarav Sharma")))
                .andExpect(jsonPath("$.data.assignedAt", notNullValue()));
    }

    @Test
    @DisplayName("05. Reject assignment to an inactive volunteer")
    void test05_rejectAssignmentToInactiveVolunteer() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("15.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, inactiveVolunteerUserId, "Test notes");

        mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Cannot assign delivery task to an inactive or non-volunteer user")));
    }

    @Test
    @DisplayName("06. Prevent unauthorized donor assignment")
    void test06_preventUnauthorizedDonorAssignment() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("18.00"));

        // Donor 2 attempts to assign volunteer to Donor 1's allocation
        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Unauthorized assignment");

        mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Only the donating merchant or an administrator can assign volunteers")));
    }

    @Test
    @DisplayName("07. Volunteer sees only their own tasks")
    void test07_volunteerSeesOnlyTheirOwnTasks() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("22.00"));

        // Assign to Volunteer 1 (Aarav, user 6)
        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "For Aarav only");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Volunteer 1 can view
        mockMvc.perform(get("/api/v1/deliveries/" + deliveryId)
                        .header("Authorization", "Bearer " + volunteer1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(deliveryId.intValue())));

        // Volunteer 2 receives 403 Forbidden
        mockMvc.perform(get("/api/v1/deliveries/" + deliveryId)
                        .header("Authorization", "Bearer " + volunteer2Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("You can only view delivery tasks assigned to you")));
    }

    @Test
    @DisplayName("08. Volunteer accepts an assignment")
    void test08_volunteerAcceptsAssignment() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("28.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Standard delivery");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Volunteer 1 accepts
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("ACCEPTED")))
                .andExpect(jsonPath("$.data.acceptedAt", notNullValue()))
                .andExpect(jsonPath("$.data.progressPercentage", is(40)));
    }

    @Test
    @DisplayName("09. Volunteer rejects an assignment")
    void test09_volunteerRejectsAssignment() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("16.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Standard delivery");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Volunteer 1 rejects
        RejectAssignmentDTO rejectDTO = new RejectAssignmentDTO("Flat tire on two-wheeler");
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/reject")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("REJECTED")))
                .andExpect(jsonPath("$.data.issueReason", is("Flat tire on two-wheeler")));
    }

    @Test
    @DisplayName("10. Reassignment after rejection works")
    void test10_reassignmentAfterRejectionWorks() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("32.00"));

        // Assign to Volunteer 1 (Aarav)
        AssignVolunteerDTO assignDTO1 = new AssignVolunteerDTO(allocationId, 6L, "Pickup urgently");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO1)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Aarav rejects
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/reject")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RejectAssignmentDTO("Unavailable due to exam schedule"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("REJECTED")));

        // Donor reassigns to Volunteer 2 (Pooja, user 7)
        AssignVolunteerDTO assignDTO2 = new AssignVolunteerDTO(allocationId, 7L, "Reassigned to Pooja");
        mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ASSIGNED")))
                .andExpect(jsonPath("$.data.volunteer.userId", is(7)))
                .andExpect(jsonPath("$.data.volunteer.fullName", is("Pooja Deshmukh")));
    }

    @Test
    @DisplayName("11. Only assigned volunteer can confirm pickup")
    void test11_onlyAssignedVolunteerCanConfirmPickup() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("20.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Assigned to Aarav");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Aarav accepts
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token))
                .andExpect(status().isOk());

        // Pooja attempts to confirm pickup $\to$ receives 403 Forbidden
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PickupDeliveryDTO("123456", "Wrong volunteer"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Only the assigned volunteer can confirm pickup")));
    }

    @Test
    @DisplayName("12. Pickup before acceptance is rejected")
    void test12_pickupBeforeAcceptanceIsRejected() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("24.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Pending acceptance");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Aarav calls pickup directly without accepting first
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PickupDeliveryDTO(null, "Skipped acceptance"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Pickup cannot occur before the volunteer has accepted")));
    }

    @Test
    @DisplayName("13. Pickup timestamp is persisted and updates allocation to IN_TRANSIT")
    void test13_pickupTimestampIsPersisted() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("35.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Pickup verification test");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Accept
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token))
                .andExpect(status().isOk());

        // Get pickup OTP
        DeliveryTask task = deliveryTaskRepository.findById(deliveryId).orElseThrow();
        String pickupOtp = task.getPickupOtp();

        // Confirm pickup
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PickupDeliveryDTO(pickupOtp, "All 35 boxes verified hot"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("PICKED_UP")))
                .andExpect(jsonPath("$.data.pickedUpAt", notNullValue()))
                .andExpect(jsonPath("$.data.notes", containsString("[Pickup]: All 35 boxes verified hot")));

        // Verify allocation status transitioned to IN_TRANSIT
        Allocation alloc = allocationRepository.findById(allocationId).orElseThrow();
        assertThat(alloc.getStatus()).isEqualTo(AllocationStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("14. Volunteer can start transit after pickup")
    void test14_volunteerCanStartTransitAfterPickup() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("26.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Transit test");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Accept & Pickup
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // Start transit
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/start-transit")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StartTransitDTO("Departed Connaught Place kitchen"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("IN_TRANSIT")))
                .andExpect(jsonPath("$.data.inTransitAt", notNullValue()))
                .andExpect(jsonPath("$.data.progressPercentage", is(85)));
    }

    @Test
    @DisplayName("15. Delivery cannot complete before pickup")
    void test15_deliveryCannotCompleteBeforePickup() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("30.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Premature completion test");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Accept
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // Try to complete delivery before pickup
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO("123456", "Rajesh", "9876543210", "Premature"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Delivery cannot be completed before food pickup has been confirmed")));
    }

    @Test
    @DisplayName("16. Delivery completion succeeds after valid transitions")
    void test16_deliveryCompletionSucceedsAfterValidTransitions() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("50.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "End-to-end completion run");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // 1. Accept
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // 2. Pickup
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // 3. Start Transit
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/start-transit")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // 4. Complete Delivery
        DeliveryTask task = deliveryTaskRepository.findById(deliveryId).orElseThrow();
        String deliveryOtp = task.getDeliveryOtp();

        CompleteDeliveryDTO compDTO = new CompleteDeliveryDTO(
                deliveryOtp,
                "Suresh Kumar (NGO Caretaker)",
                "9811223344",
                "Handed over in person to Robin Hood Army team. Food warm and intact."
        );

        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("DELIVERED")))
                .andExpect(jsonPath("$.data.deliveredAt", notNullValue()))
                .andExpect(jsonPath("$.data.recipientName", is("Suresh Kumar (NGO Caretaker)")))
                .andExpect(jsonPath("$.data.progressPercentage", is(100)));

        // Verify allocation status is DELIVERED
        Allocation alloc = allocationRepository.findById(allocationId).orElseThrow();
        assertThat(alloc.getStatus()).isEqualTo(AllocationStatus.DELIVERED);
    }

    @Test
    @DisplayName("17. Only assigned volunteer can complete delivery")
    void test17_onlyAssignedVolunteerCanCompleteDelivery() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("20.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Authorization completion guard");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Aarav accepts and picks up
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // Pooja attempts to complete delivery $\to$ receives 403 Forbidden
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                        .header("Authorization", "Bearer " + volunteer2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO("123456", "Test", "123", "Note"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Only the assigned volunteer can complete delivery")));
    }

    @Test
    @DisplayName("18. Completed delivery cannot be reopened")
    void test18_completedDeliveryCannotBeReopened() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("15.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Reopening guard test");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                .header("Authorization", "Bearer " + volunteer1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO(null, "Rep", "123", "Done")))).andExpect(status().isOk());

        // Attempting to complete again throws 400
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO(null, "Rep", "123", "Repeat"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Completed delivery cannot be reopened")));
    }

    @Test
    @DisplayName("19. Cancelled or failed tasks cannot be completed")
    void test19_cancelledOrFailedTasksCannotBeCompleted() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("18.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Failure guard test");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Report issue $\to$ marks FAILED
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/report-issue")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReportIssueDTO("Vehicle breakdown during route", "Cannot continue"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("FAILED")));

        // Attempting to complete delivery on failed task throws 400
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO(null, "Rep", "123", "Try complete"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Cancelled or failed delivery tasks cannot be completed")));
    }

    @Test
    @DisplayName("20. Delivery history remains persisted in PostgreSQL")
    void test20_deliveryHistoryRemainsPersisted() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("25.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Persistence verification");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/start-transit")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // Check direct DB persistence
        DeliveryTask task = deliveryTaskRepository.findById(deliveryId).orElseThrow();
        assertThat(task.getStatus()).isEqualTo(DeliveryStatus.IN_TRANSIT);
        assertThat(task.getAssignedAt()).isNotNull();
        assertThat(task.getAcceptedAt()).isNotNull();
        assertThat(task.getPickedUpAt()).isNotNull();
        assertThat(task.getInTransitAt()).isNotNull();
        assertThat(task.getVolunteer().getUserId()).isEqualTo(6L);
    }

    @Test
    @DisplayName("21. Allocation quantity and donation stock are NOT deducted a second time during pickup or delivery")
    void test21_allocationQuantityAndStockNotDeductedSecondTime() throws Exception {
        BigDecimal initialDonationQty = new BigDecimal("40.00");
        Long allocationId = createFreshAllocation(donorToken1, initialDonationQty);

        Allocation allocBefore = allocationRepository.findByIdWithDetails(allocationId).orElseThrow();
        FoodDonation donBefore = allocBefore.getDonation();
        BigDecimal availableQtyBefore = donBefore.getAvailableQuantity();
        BigDecimal allocatedQtyBefore = allocBefore.getAllocatedQuantity();

        // Execute full delivery lifecycle
        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Stock invariant verification");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/complete")
                .header("Authorization", "Bearer " + volunteer1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CompleteDeliveryDTO(null, "NGO Rep", "9876543210", "Stock verified")))).andExpect(status().isOk());

        // Re-read DB
        Allocation allocAfter = allocationRepository.findByIdWithDetails(allocationId).orElseThrow();
        FoodDonation donAfter = allocAfter.getDonation();

        // INVARIANT: Quantities must remain exactly identical
        assertThat(allocAfter.getAllocatedQuantity()).isEqualByComparingTo(allocatedQtyBefore);
        assertThat(donAfter.getAvailableQuantity()).isEqualByComparingTo(availableQtyBefore);
    }

    @Test
    @DisplayName("22. Failed state transitions do not partially update related records")
    void test22_failedStateTransitionsDoNotPartiallyUpdateRelatedRecords() throws Exception {
        Long allocationId = createFreshAllocation(donorToken1, new BigDecimal("25.00"));

        AssignVolunteerDTO assignDTO = new AssignVolunteerDTO(allocationId, 6L, "Partial rollback check");
        MvcResult assignRes = mockMvc.perform(post("/api/v1/deliveries/assign")
                        .header("Authorization", "Bearer " + donorToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andReturn();
        Long deliveryId = objectMapper.readTree(assignRes.getResponse().getContentAsString()).path("data").path("id").asLong();

        // Aarav accepts
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/accept")
                .header("Authorization", "Bearer " + volunteer1Token)).andExpect(status().isOk());

        // Attempt pickup with WRONG OTP
        mockMvc.perform(post("/api/v1/deliveries/" + deliveryId + "/pickup")
                        .header("Authorization", "Bearer " + volunteer1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PickupDeliveryDTO("000000", "Bad OTP attempt"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid pickup verification code")));

        // Invariant: Status must still be ACCEPTED, NOT PICKED_UP, and Allocation still AWAITING_PICKUP
        DeliveryTask task = deliveryTaskRepository.findById(deliveryId).orElseThrow();
        assertThat(task.getStatus()).isEqualTo(DeliveryStatus.ACCEPTED);
        assertThat(task.getPickedUpAt()).isNull();

        Allocation alloc = allocationRepository.findById(allocationId).orElseThrow();
        assertThat(alloc.getStatus()).isEqualTo(AllocationStatus.AWAITING_PICKUP);
    }
}
