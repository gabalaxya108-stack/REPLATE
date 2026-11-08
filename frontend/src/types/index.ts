export type UserRole = 'DONOR' | 'NGO' | 'VOLUNTEER' | 'ADMIN';

export type FoodCategory =
  | 'COOKED_MEALS'
  | 'BREADS_ROTIS'
  | 'DAIRY_SWEETS'
  | 'RAW_PRODUCE'
  | 'PACKAGED_GOODS';

export type QuantityUnit = 'KG' | 'MEAL_BOXES' | 'LITERS' | 'PORTIONS';

export type DietaryType = 'PURE_VEG' | 'NON_VEG' | 'VEGAN';

export type StorageCondition = 'HOT_INSULATED' | 'REFRIGERATED' | 'AMBIENT';

export type DonationStatus =
  | 'AVAILABLE'
  | 'PARTIALLY_ALLOCATED'
  | 'FULLY_ALLOCATED'
  | 'COMPLETED'
  | 'EXPIRED'
  | 'CANCELLED';

export interface User {
  id: number;
  email: string;
  fullName: string;
  phoneNumber: string;
  role: UserRole;
  status: string;
  city: string;
  organizationName?: string;
  fssaiLicenseNo?: string;
  darpanId?: string;
  vehicleType?: string;
}

export interface FoodDonationItem {
  id: number;
  donorName: string;
  donorCity: string;
  title: string;
  category: FoodCategory;
  totalQuantity: number;
  availableQuantity: number;
  quantityUnit: QuantityUnit;
  dietaryType: DietaryType;
  storageCondition: StorageCondition;
  preparationTime: string;
  expiryTime: string;
  pickupNotes: string;
  status: DonationStatus;
  createdAt: string;
}

