export type DeliveryStatus =
  | 'PENDING_ASSIGNMENT'
  | 'UNASSIGNED'
  | 'ASSIGNED'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'PICKED_UP'
  | 'IN_TRANSIT'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'FAILED';

export interface VolunteerSummary {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  vehicleType: string;
  emergencyContact: string;
  totalDeliveries: number;
  city?: string;
  state?: string;
  status?: string;
}

export interface DeliveryDonationSnapshot {
  id: number;
  title: string;
  category: string;
  pickupNotes?: string;
}

export interface DeliveryDonorSnapshot {
  userId: number;
  organizationName: string;
  contactPerson: string;
  phoneNumber: string;
  addressLine1: string;
  city: string;
  state: string;
  pincode: string;
}

export interface DeliveryNgoSnapshot {
  userId: number;
  ngoName: string;
  contactPerson: string;
  phoneNumber: string;
  addressLine1: string;
  city: string;
  state: string;
  pincode: string;
}

export interface DeliveryTaskResponse {
  id: number;
  allocationId: number;
  allocatedQuantity: number;
  quantityUnit: string;
  status: DeliveryStatus;
  statusDescription: string;
  progressPercentage: number;
  pickupOtp?: string;
  deliveryOtp?: string;
  assignedAt?: string;
  acceptedAt?: string;
  pickedUpAt?: string;
  inTransitAt?: string;
  deliveredAt?: string;
  recipientName?: string;
  recipientPhone?: string;
  notes?: string;
  issueReason?: string;
  createdAt: string;
  updatedAt: string;
  donation?: DeliveryDonationSnapshot;
  donor?: DeliveryDonorSnapshot;
  ngo?: DeliveryNgoSnapshot;
  volunteer?: VolunteerSummary;
}

export interface AssignVolunteerRequest {
  allocationId: number;
  volunteerId: number;
  notes?: string;
}

export interface PickupDeliveryRequest {
  otp?: string;
  notes?: string;
}

export interface StartTransitRequest {
  notes?: string;
}

export interface CompleteDeliveryRequest {
  otp?: string;
  recipientName?: string;
  recipientPhone?: string;
  notes?: string;
}

export interface ReportIssueRequest {
  reason: string;
  notes?: string;
}

export interface RejectAssignmentRequest {
  reason?: string;
}

