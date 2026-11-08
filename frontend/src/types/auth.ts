import { UserRole } from './index';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  phoneNumber: string;
  role: UserRole;
  addressLine1: string;
  city: string;
  state: string;
  pincode: string;
  latitude?: number;
  longitude?: number;

  // Donor
  organizationName?: string;
  organizationType?: string;
  fssaiLicenseNo?: string;
  operatingHours?: string;

  // NGO
  ngoName?: string;
  darpanId?: string;
  registration80g?: string;
  dailyBeneficiaryCapacity?: number;

  // Volunteer
  vehicleType?: string;
  emergencyContact?: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  userId: number;
  fullName: string;
  email: string;
  role: UserRole;
  city: string;
}

export interface UserSummary {
  id: number;
  email: string;
  fullName: string;
  phoneNumber: string;
  role: UserRole;
  status: string;
  addressLine1?: string;
  city?: string;
  state?: string;
  pincode?: string;
  profileDetails?: Record<string, any>;
}
