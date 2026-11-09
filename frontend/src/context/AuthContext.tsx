import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { api, ApiError } from '../services/api';
import { AuthResponse, LoginRequest, RegisterRequest, UserSummary } from '../types/auth';

interface AuthContextType {
  user: UserSummary | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (credentials: LoginRequest) => Promise<AuthResponse>;
  register: (payload: RegisterRequest) => Promise<AuthResponse>;
  logout: () => void;
  clearError: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserSummary | null>(null);
  const [token, setToken] = useState<string | null>(() => {
    try {
      return localStorage.getItem('replate_access_token');
    } catch {
      return null;
    }
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const clearError = () => setError(null);

  const refreshUser = async () => {
    try {
      const currentUser = await api.getCurrentUser();
      setUser(currentUser);
    } catch (err: any) {
      // If token expired or invalid, log out cleanly
      logout();
    }
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('replate_access_token');
      if (storedToken) {
        try {
          const currentUser = await api.getCurrentUser();
          setUser(currentUser);
          setToken(storedToken);
        } catch (err: any) {
          localStorage.removeItem('replate_access_token');
          setToken(null);
          setUser(null);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
    setError(null);
    try {
      const authData = await api.login(credentials);
      localStorage.setItem('replate_access_token', authData.accessToken);
      setToken(authData.accessToken);

      // Hydrate full user details
      try {
        const fullUser = await api.getCurrentUser();
        setUser(fullUser);
      } catch {
        // Fallback to basic summary from AuthResponse
        setUser({
          id: authData.userId,
          fullName: authData.fullName,
          email: authData.email,
          phoneNumber: '',
          role: authData.role,
          status: 'ACTIVE',
          city: authData.city,
        });
      }

      return authData;
    } catch (err: any) {
      const message = err instanceof ApiError ? err.message : 'Login failed. Please check your credentials.';
      setError(message);
      throw err;
    }
  };

  const register = async (payload: RegisterRequest): Promise<AuthResponse> => {
    setError(null);
    try {
      const authData = await api.register(payload);
      localStorage.setItem('replate_access_token', authData.accessToken);
      setToken(authData.accessToken);

      // Hydrate full user details
      try {
        const fullUser = await api.getCurrentUser();
        setUser(fullUser);
      } catch {
        setUser({
          id: authData.userId,
          fullName: authData.fullName,
          email: authData.email,
          phoneNumber: payload.phoneNumber,
          role: authData.role,
          status: 'ACTIVE',
          addressLine1: payload.addressLine1,
          city: payload.city,
          state: payload.state,
          pincode: payload.pincode,
        });
      }

      return authData;
    } catch (err: any) {
      const message = err instanceof ApiError ? err.message : 'Registration failed. Please check your inputs.';
      setError(message);
      throw err;
    }
  };

  const logout = () => {
    try {
      localStorage.removeItem('replate_access_token');
    } catch {
      // ignore
    }
    setToken(null);
    setUser(null);
    setError(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        isLoading,
        error,
        login,
        register,
        logout,
        clearError,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

