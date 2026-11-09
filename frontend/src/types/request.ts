import { FoodCategory, QuantityUnit, DietaryType, StorageCondition } from './index';

export type RequestStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'WITHDRAWN'
  | 'CANCELLED'
  | 'EXPIRED';

export interface FoodRequestResponse {
  id: number;
  donationId: number;
  donationTitle: string;
  donationCategory: FoodCategory;
  donationTotalQuantity: number;
  donationAvailableQuantity: number;
  donationQuantityUnit: QuantityUnit;
  donationDietaryType: DietaryType;
  donationStorageCondition: StorageCondition;
  donationExpiryTime: string;

  donorId: number;
  donorName: string;
  donorCity: string;
  donorPhone?: string;

  ngoId: number;
  ngoName: string;
  ngoDarpanId?: string;
  ngoPhone?: string;

  requestedQuantity: number;
  intendedBeneficiaries: string;
  status: RequestStatus;
  rejectionReason?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFoodRequest {
  donationId: number;
  requestedQuantity: number;
  intendedBeneficiaries: string;
}

export interface RejectFoodRequest {
  rejectionReason: string;
}
