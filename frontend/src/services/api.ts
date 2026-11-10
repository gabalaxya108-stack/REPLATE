import { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, UserSummary } from '../types/auth';
import { CreateDonationRequest, DonationResponse, PageResponse, UpdateDonationRequest } from '../types/donation';
import { CreateFoodRequest, FoodRequestResponse, RejectFoodRequest } from '../types/request';
import { AllocationResponse, CreateAllocationRequest } from '../types/allocation';

const BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || '/api/v1';

export class ApiError extends Error {
  status: number;
  data?: any;

  constructor(message: string, status: number, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

class ApiClient {
  private getToken(): string | null {
    try {
      return localStorage.getItem('replate_access_token');
    } catch {
      return null;
    }
  }

  async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const token = this.getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(options.headers as Record<string, string>),
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const url = endpoint.startsWith('http') ? endpoint : `${BASE_URL}${endpoint}`;

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      const json: ApiResponse<T> = await response.json().catch(() => ({
        success: false,
        message: response.statusText || 'Unknown server response',
        data: null as any,
        timestamp: new Date().toISOString(),
      }));

      if (!response.ok || !json.success) {
        throw new ApiError(
          json.message || `Request failed with status ${response.status}`,
          response.status,
          json.data
        );
      }

      return json.data;
    } catch (err: any) {
      if (err instanceof ApiError) {
        throw err;
      }
      throw new ApiError(
        err.message || 'Unable to connect to the backend server. Please verify Spring Boot is running on port 8080.',
        0
      );
    }
  }

  // Authentication endpoints
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    return this.request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  }

  async register(payload: RegisterRequest): Promise<AuthResponse> {
    return this.request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  async getCurrentUser(): Promise<UserSummary> {
    return this.request<UserSummary>('/auth/me', {
      method: 'GET',
    });
  }

  // Food Donation Endpoints
  async createDonation(payload: CreateDonationRequest): Promise<DonationResponse> {
    return this.request<DonationResponse>('/donations', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  async updateDonation(id: number, payload: UpdateDonationRequest): Promise<DonationResponse> {
    return this.request<DonationResponse>(`/donations/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  }

  async publishDonation(id: number): Promise<DonationResponse> {
    return this.request<DonationResponse>(`/donations/${id}/publish`, {
      method: 'POST',
    });
  }

  async cancelDonation(id: number): Promise<DonationResponse> {
    return this.request<DonationResponse>(`/donations/${id}/cancel`, {
      method: 'POST',
    });
  }

  async getMyDonations(page = 0, size = 10): Promise<PageResponse<DonationResponse>> {
    return this.request<PageResponse<DonationResponse>>(`/donations/mine?page=${page}&size=${size}`, {
      method: 'GET',
    });
  }

  async searchPublicDonations(params: {
    category?: string;
    dietaryType?: string;
    city?: string;
    query?: string;
    page?: number;
    size?: number;
  } = {}): Promise<PageResponse<DonationResponse>> {
    const searchParams = new URLSearchParams();
    if (params.category && params.category !== 'ALL') searchParams.set('category', params.category);
    if (params.dietaryType && params.dietaryType !== 'ALL') searchParams.set('dietaryType', params.dietaryType);
    if (params.city && params.city !== 'ALL') searchParams.set('city', params.city);
    if (params.query && params.query.trim()) searchParams.set('query', params.query.trim());
    if (params.page !== undefined) searchParams.set('page', params.page.toString());
    if (params.size !== undefined) searchParams.set('size', params.size.toString());

    const qs = searchParams.toString();
    const endpoint = `/donations/public${qs ? `?${qs}` : ''}`;
    return this.request<PageResponse<DonationResponse>>(endpoint, {
      method: 'GET',
    });
  }

  async getDonationById(id: number): Promise<DonationResponse> {
    return this.request<DonationResponse>(`/donations/${id}`, {
      method: 'GET',
    });
  }

  async getPublicDonationById(id: number): Promise<DonationResponse> {
    return this.request<DonationResponse>(`/donations/public/${id}`, {
      method: 'GET',
    });
  }

  // Food Request Endpoints
  async createFoodRequest(payload: CreateFoodRequest): Promise<FoodRequestResponse> {
    return this.request<FoodRequestResponse>('/requests', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  async getMyFoodRequests(page = 0, size = 10): Promise<PageResponse<FoodRequestResponse>> {
    return this.request<PageResponse<FoodRequestResponse>>(`/requests/mine?page=${page}&size=${size}`, {
      method: 'GET',
    });
  }

  async getReceivedFoodRequests(page = 0, size = 10): Promise<PageResponse<FoodRequestResponse>> {
    return this.request<PageResponse<FoodRequestResponse>>(`/requests/received?page=${page}&size=${size}`, {
      method: 'GET',
    });
  }

  async getFoodRequestById(id: number): Promise<FoodRequestResponse> {
    return this.request<FoodRequestResponse>(`/requests/${id}`, {
      method: 'GET',
    });
  }

  async approveFoodRequest(id: number, remarks?: string): Promise<FoodRequestResponse> {
    const qs = remarks ? `?remarks=${encodeURIComponent(remarks)}` : '';
    return this.request<FoodRequestResponse>(`/requests/${id}/approve${qs}`, {
      method: 'POST',
    });
  }

  async rejectFoodRequest(id: number, payload: RejectFoodRequest): Promise<FoodRequestResponse> {
    return this.request<FoodRequestResponse>(`/requests/${id}/reject`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  async withdrawFoodRequest(id: number): Promise<FoodRequestResponse> {
    return this.request<FoodRequestResponse>(`/requests/${id}/withdraw`, {
      method: 'POST',
    });
  }

  // Food Allocation Endpoints
  async createAllocation(payload: CreateAllocationRequest): Promise<AllocationResponse> {
    return this.request<AllocationResponse>('/allocations', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  async getMyAllocations(page = 0, size = 10): Promise<PageResponse<AllocationResponse>> {
    return this.request<PageResponse<AllocationResponse>>(`/allocations/mine?page=${page}&size=${size}`, {
      method: 'GET',
    });
  }

  async getAllocationById(id: number): Promise<AllocationResponse> {
    return this.request<AllocationResponse>(`/allocations/${id}`, {
      method: 'GET',
    });
  }

  async getAllocationsByDonation(donationId: number): Promise<AllocationResponse[]> {
    return this.request<AllocationResponse[]>(`/allocations/donation/${donationId}`, {
      method: 'GET',
    });
  }

  async getAllocationsByRequest(requestId: number): Promise<AllocationResponse[]> {
    return this.request<AllocationResponse[]>(`/allocations/request/${requestId}`, {
      method: 'GET',
    });
  }
}

export const api = new ApiClient();
