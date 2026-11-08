import { ApiResponse, AuthResponse, LoginRequest, RegisterRequest, UserSummary } from '../types/auth';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

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
}

export const api = new ApiClient();
