import { FoodCategory, QuantityUnit, DietaryType, StorageCondition } from './index';

export type AllocationStatus =
  | 'AWAITING_PICKUP'
  | 'IN_TRANSIT'
  | 'DELIVERED'
  | 'CANCELLED';

export interface AllocationResponse {
  id: number;
  requestId: number;
  donationId: number;
  donationTitle: string;
  donationCategory: FoodCategory;
  quantityUnit: QuantityUnit;
  dietaryType: DietaryType;
  storageCondition: StorageCondition;
  allocatedQuantity: number;
  status: AllocationStatus;

  donorId: number;
  donorName: string;
  donorCity: string;
  donorPhone?: string;

  ngoId: number;
  ngoName: string;
  ngoDarpanId?: string;
  ngoPhone?: string;

  allocatedById?: number;
  allocatedByName?: string;

  requestRequestedQuantity: number;
  requestIntendedBeneficiaries: string;
  donationAvailableQuantity: number;
  donationTotalQuantity: number;

  createdAt: string;
  updatedAt: string;
}

export interface CreateAllocationRequest {
  requestId: number;
  allocatedQuantity?: number;
  notes?: string;
}
