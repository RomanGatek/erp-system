import { auth } from '@/services/auth'
import { api } from '@/services/api'
import {
  beforeEach,
  describe,
  it,
  expect,
  vi
} from 'vitest'

// Create a manual mock for api
vi.mock('@/services/api', () => {
  return {
    api: {
      post: vi.fn()
    }
  }
})

describe('Auth service', () => {
  // Reset all mocks before each test
  beforeEach(() => {
    vi.clearAllMocks();
  })

  describe('login', () => {
    it('should return accessToken on successful login', async () => {
      // Arrange
      const email = 'admin@example.com';
      const password = 'P&ssw0rd123@';
      const credentials = { email, password };
      const mockResponse = { data: { accessToken: 'JWT_KEY', refreshToken: 'REFRESH_TOKEN' } };

      // Set up the mock to return a resolved promise
      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.login(credentials);

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/public/login', JSON.stringify(credentials));
      expect(result).toBe('JWT_KEY');
    });

    it('should return null if response does not contain accessToken', async () => {
      // Arrange
      const credentials = { email: 'user@example.com', password: 'password' };
      const mockResponse = { data: {} };

      // Set up the mock to return a resolved promise
      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.login(credentials);

      // Assert
      expect(result).toBeNull();
    });

    it('should propagate error on failed login', async () => {
      // Arrange
      const credentials = { email: 'wrong@example.com', password: 'wrongpassword' };
      const mockError = new Error('Unauthorized');
      mockError.response = { status: 401 };

      // Set up the mock to return a rejected promise
      api.post.mockImplementation(() => Promise.reject(mockError));

      // Act & Assert
      await expect(auth.login(credentials)).rejects.toThrow('Unauthorized');
    });
  });

  describe('register', () => {
    it('should register user correctly', async () => {
      // Arrange
      const userData = { email: 'new@example.com', password: 'password', name: 'New User' };
      const mockResponse = { data: { id: '123', email: 'new@example.com' } };

      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.register(userData);

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/public/signup', userData);
      expect(result).toEqual(mockResponse.data);
    });

    it('should propagate error on failed registration', async () => {
      // Arrange
      const userData = { email: 'existing@example.com', password: 'password', name: 'User' };
      const mockError = new Error('Email already exists');

      api.post.mockImplementation(() => Promise.reject(mockError));

      // Act & Assert
      await expect(auth.register(userData)).rejects.toThrow('Email already exists');
    });
  });

  describe('logout', () => {
    it('should log out user correctly', async () => {
      // Arrange
      const mockResponse = { data: { success: true } };

      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.logout();

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/public/logout');
      expect(result).toEqual(mockResponse.data);
    });

    it('should propagate error on failed logout', async () => {
      // Arrange
      const mockError = new Error('Session expired');

      api.post.mockImplementation(() => Promise.reject(mockError));

      // Act & Assert
      await expect(auth.logout()).rejects.toThrow('Session expired');
    });
  });

  describe('forgotPassword', () => {
    it('should send password reset request', async () => {
      // Arrange
      const email = 'user@example.com';
      const mockResponse = { data: { success: true, message: 'Email sent' } };

      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.forgotPassword(email);

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/public/forgot-password', { email });
      expect(result).toEqual(mockResponse.data);
    });

    it('should propagate error when email does not exist', async () => {
      // Arrange
      const email = 'nonexistent@example.com';
      const mockError = new Error('Email not found');

      api.post.mockImplementation(() => Promise.reject(mockError));

      // Act & Assert
      await expect(auth.forgotPassword(email)).rejects.toThrow('Email not found');
    });
  });

  describe('resetPassword', () => {
    it('should reset password successfully', async () => {
      // Arrange
      const token = 'valid-token';
      const newPassword = 'NewPassword123';
      const mockResponse = { data: { success: true } };

      api.post.mockImplementation(() => Promise.resolve(mockResponse));

      // Act
      const result = await auth.resetPassword(token, newPassword);

      // Assert
      expect(api.post).toHaveBeenCalledWith('/auth/public/reset-password', { token, newPassword });
      expect(result).toEqual(mockResponse.data);
    });

    it('should propagate error for invalid token', async () => {
      // Arrange
      const token = 'invalid-token';
      const newPassword = 'NewPassword123';
      const mockError = new Error('Invalid or expired token');

      api.post.mockImplementation(() => Promise.reject(mockError));

      // Act & Assert
      await expect(auth.resetPassword(token, newPassword)).rejects.toThrow('Invalid or expired token');
    });
  });
});
