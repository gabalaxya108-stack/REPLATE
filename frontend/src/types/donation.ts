import { FoodCategory, QuantityUnit, DietaryType, StorageCondition, DonationStatus } from './index';

export interface DonationResponse {
  id: number;
  donorId: number;
  donorName: string;
  donorCity: string;
  donorAddress?: string;
  donorPhone?: string;
  title: string;
  category: FoodCategory;
  totalQuantity: number;
  availableQuantity: number;
  quantityUnit: QuantityUnit;
  dietaryType: DietaryType;
  storageCondition: StorageCondition;
  preparationTime: string;
  expiryTime: string;
  pickupNotes?: string;
  status: DonationStatus;
  statusDescription: string;
  progressPercentage: number;
  terminal: boolean;
  expired: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDonationRequest {
  title: string;
  category: FoodCategory;
  totalQuantity: number;
  quantityUnit: QuantityUnit;
  dietaryType: DietaryType;
  storageCondition: StorageCondition;
  preparationTime: string;
  expiryTime: string;
  pickupNotes?: string;
  publishImmediately?: boolean;
}

export interface UpdateDonationRequest {
  title: string;
  category: FoodCategory;
  totalQuantity: number;
  quantityUnit: QuantityUnit;
  dietaryType: DietaryType;
  storageCondition: StorageCondition;
  preparationTime: string;
  expiryTime: string;
  pickupNotes?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

