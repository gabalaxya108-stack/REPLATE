package com.replate.dto.response;

import com.replate.domain.entity.*;
import java.math.BigDecimal;
import java.time.Instant;

public class DeliveryTaskResponseDTO {

    private Long id;
    private Long allocationId;
    private BigDecimal allocatedQuantity;
    private String quantityUnit;
    private String status;
    private String statusDescription;
    private int progressPercentage;
    private String pickupOtp;
    private String deliveryOtp;
    private Instant assignedAt;
    private Instant acceptedAt;
    private Instant pickedUpAt;
    private Instant inTransitAt;
    private Instant deliveredAt;
    private String recipientName;
    private String recipientPhone;
    private String notes;
    private String issueReason;
    private Instant createdAt;
    private Instant updatedAt;

    // Nested domain snapshots
    private DonationSnapshot donation;
    private DonorSnapshot donor;
    private NgoSnapshot ngo;
    private VolunteerSummaryDTO volunteer;

    public DeliveryTaskResponseDTO() {}

    public static DeliveryTaskResponseDTO fromEntity(DeliveryTask task) {
        if (task == null) return null;

        DeliveryTaskResponseDTO dto = new DeliveryTaskResponseDTO();
        dto.setId(task.getId());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setStatusDescription(task.getCurrentStatusDescription());
        dto.setProgressPercentage(task.getProgressPercentage());
        dto.setPickupOtp(task.getPickupOtp());
        dto.setDeliveryOtp(task.getDeliveryOtp());
        dto.setAssignedAt(task.getAssignedAt());
        dto.setAcceptedAt(task.getAcceptedAt());
        dto.setPickedUpAt(task.getPickedUpAt());
        dto.setInTransitAt(task.getInTransitAt());
        dto.setDeliveredAt(task.getDeliveredAt());
        dto.setRecipientName(task.getRecipientName());
        dto.setRecipientPhone(task.getRecipientPhone());
        dto.setNotes(task.getNotes());
        dto.setIssueReason(task.getIssueReason());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getVolunteer() != null) {
            dto.setVolunteer(VolunteerSummaryDTO.fromEntity(task.getVolunteer()));
        }

        Allocation alloc = task.getAllocation();
        if (alloc != null) {
            dto.setAllocationId(alloc.getId());
            dto.setAllocatedQuantity(alloc.getAllocatedQuantity());

            FoodDonation f = alloc.getDonation();
            if (f != null) {
                dto.setQuantityUnit(f.getQuantityUnit() != null ? f.getQuantityUnit().name() : "KG");
                dto.setDonation(new DonationSnapshot(
                        f.getId(),
                        f.getTitle(),
                        f.getCategory() != null ? f.getCategory().name() : null,
                        f.getPickupNotes()
                ));

                DonorProfile dp = f.getDonor();
                if (dp != null && dp.getUser() != null) {
                    BaseUser du = dp.getUser();
                    LocationCoordinate loc = du.getLocation();
                    dto.setDonor(new DonorSnapshot(
                            du.getId(),
                            dp.getOrganizationName(),
                            du.getFullName(),
                            du.getPhoneNumber(),
                            loc != null ? loc.getAddressLine1() : null,
                            loc != null ? loc.getCity() : null,
                            loc != null ? loc.getState() : null,
                            loc != null ? loc.getPincode() : null
                    ));
                }
            }

            FoodRequest r = alloc.getRequest();
            if (r != null && r.getNgo() != null) {
                NgoProfile np = r.getNgo();
                BaseUser nu = np.getUser();
                if (nu != null) {
                    LocationCoordinate loc = nu.getLocation();
                    dto.setNgo(new NgoSnapshot(
                            nu.getId(),
                            np.getNgoName(),
                            nu.getFullName(),
                            nu.getPhoneNumber(),
                            loc != null ? loc.getAddressLine1() : null,
                            loc != null ? loc.getCity() : null,
                            loc != null ? loc.getState() : null,
                            loc != null ? loc.getPincode() : null
                    ));
                }
            }
        }

        return dto;
    }

    // Nested static snapshots
    public static class DonationSnapshot {
        private Long id;
        private String title;
        private String category;
        private String pickupNotes;

        public DonationSnapshot() {}
        public DonationSnapshot(Long id, String title, String category, String pickupNotes) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.pickupNotes = pickupNotes;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getPickupNotes() { return pickupNotes; }
        public void setPickupNotes(String pickupNotes) { this.pickupNotes = pickupNotes; }
    }

    public static class DonorSnapshot {
        private Long userId;
        private String organizationName;
        private String contactPerson;
        private String phoneNumber;
        private String addressLine1;
        private String city;
        private String state;
        private String pincode;

        public DonorSnapshot() {}
        public DonorSnapshot(Long userId, String organizationName, String contactPerson, String phoneNumber,
                             String addressLine1, String city, String state, String pincode) {
            this.userId = userId;
            this.organizationName = organizationName;
            this.contactPerson = contactPerson;
            this.phoneNumber = phoneNumber;
            this.addressLine1 = addressLine1;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public String getContactPerson() { return contactPerson; }
        public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
    }

    public static class NgoSnapshot {
        private Long userId;
        private String ngoName;
        private String contactPerson;
        private String phoneNumber;
        private String addressLine1;
        private String city;
        private String state;
        private String pincode;

        public NgoSnapshot() {}
        public NgoSnapshot(Long userId, String ngoName, String contactPerson, String phoneNumber,
                           String addressLine1, String city, String state, String pincode) {
            this.userId = userId;
            this.ngoName = ngoName;
            this.contactPerson = contactPerson;
            this.phoneNumber = phoneNumber;
            this.addressLine1 = addressLine1;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
        }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getNgoName() { return ngoName; }
        public void setNgoName(String ngoName) { this.ngoName = ngoName; }
        public String getContactPerson() { return contactPerson; }
        public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAllocationId() { return allocationId; }
    public void setAllocationId(Long allocationId) { this.allocationId = allocationId; }
    public BigDecimal getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(BigDecimal allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }
    public String getQuantityUnit() { return quantityUnit; }
    public void setQuantityUnit(String quantityUnit) { this.quantityUnit = quantityUnit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
    public String getPickupOtp() { return pickupOtp; }
    public void setPickupOtp(String pickupOtp) { this.pickupOtp = pickupOtp; }
    public String getDeliveryOtp() { return deliveryOtp; }
    public void setDeliveryOtp(String deliveryOtp) { this.deliveryOtp = deliveryOtp; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }
    public Instant getPickedUpAt() { return pickedUpAt; }
    public void setPickedUpAt(Instant pickedUpAt) { this.pickedUpAt = pickedUpAt; }
    public Instant getInTransitAt() { return inTransitAt; }
    public void setInTransitAt(Instant inTransitAt) { this.inTransitAt = inTransitAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getIssueReason() { return issueReason; }
    public void setIssueReason(String issueReason) { this.issueReason = issueReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public DonationSnapshot getDonation() { return donation; }
    public void setDonation(DonationSnapshot donation) { this.donation = donation; }
    public DonorSnapshot getDonor() { return donor; }
    public void setDonor(DonorSnapshot donor) { this.donor = donor; }
    public NgoSnapshot getNgo() { return ngo; }
    public void setNgo(NgoSnapshot ngo) { this.ngo = ngo; }
    public VolunteerSummaryDTO getVolunteer() { return volunteer; }
    public void setVolunteer(VolunteerSummaryDTO volunteer) { this.volunteer = volunteer; }
}
