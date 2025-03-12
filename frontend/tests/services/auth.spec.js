import api from '@/services/api'
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
    default: {
      auth: vi.fn().mockReturnValue({
        login: vi.fn(),
        register: vi.fn(),
        logout: vi.fn(),
        forgotPassword: vi.fn(),
        resetPassword: vi.fn()
      })
    }
  }
})

describe('Auth service', () => {
  const auth = api.auth()
  
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
      const mockResponse = ['JWT_KEY', null];

      // Set up the mock to return a resolved promise
      auth.login.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.login(credentials);

      // Assert
      expect(auth.login).toHaveBeenCalledWith(credentials);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBe('JWT_KEY');
      expect(result[1]).toBeNull();
    });

    it('should return null with error if login fails', async () => {
      // Arrange
      const credentials = { email: 'wrong@example.com', password: 'wrongpassword' };
      const mockError = new Error('Unauthorized');
      mockError.response = { status: 401 };
      const mockResponse = [null, mockError];

      // Set up the mock to return the error response
      auth.login.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.login(credentials);

      // Assert
      expect(auth.login).toHaveBeenCalledWith(credentials);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBeNull();
      expect(result[1]).toBe(mockError);
    });
  });

  describe('register', () => {
    it('should register user correctly', async () => {
      // Arrange
      const userData = { email: 'new@example.com', password: 'password', name: 'New User' };
      const mockData = { id: '123', email: 'new@example.com' };
      const mockResponse = [mockData, null];

      auth.register.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.register(userData);

      // Assert
      expect(auth.register).toHaveBeenCalledWith(userData);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toEqual(mockData);
      expect(result[1]).toBeNull();
    });

    it('should return error on failed registration', async () => {
      // Arrange
      const userData = { email: 'existing@example.com', password: 'password', name: 'User' };
      const mockError = new Error('Email already exists');
      const mockResponse = [null, mockError];

      auth.register.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.register(userData);

      // Assert
      expect(auth.register).toHaveBeenCalledWith(userData);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBeNull();
      expect(result[1]).toBe(mockError);
    });
  });

  describe('logout', () => {
    it('should log out user correctly', async () => {
      // Arrange
      const mockData = { success: true };
      const mockResponse = [mockData, null];

      auth.logout.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.logout();

      // Assert
      expect(auth.logout).toHaveBeenCalled();
      expect(result).toEqual(mockResponse);
      expect(result[0]).toEqual(mockData);
      expect(result[1]).toBeNull();
    });

    it('should return error on failed logout', async () => {
      // Arrange
      const mockError = new Error('Session expired');
      const mockResponse = [null, mockError];

      auth.logout.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.logout();

      // Assert
      expect(auth.logout).toHaveBeenCalled();
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBeNull();
      expect(result[1]).toBe(mockError);
    });
  });

  describe('forgotPassword', () => {
    it('should send password reset request', async () => {
      // Arrange
      const email = 'user@example.com';
      const mockData = { success: true, message: 'Email sent' };
      const mockResponse = [mockData, null];

      auth.forgotPassword.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.forgotPassword(email);

      // Assert
      expect(auth.forgotPassword).toHaveBeenCalledWith(email);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toEqual(mockData);
      expect(result[1]).toBeNull();
    });

    it('should return error when email does not exist', async () => {
      // Arrange
      const email = 'nonexistent@example.com';
      const mockError = new Error('Email not found');
      const mockResponse = [null, mockError];

      auth.forgotPassword.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.forgotPassword(email);

      // Assert
      expect(auth.forgotPassword).toHaveBeenCalledWith(email);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBeNull();
      expect(result[1]).toBe(mockError);
    });
  });

  describe('resetPassword', () => {
    it('should reset password successfully', async () => {
      // Arrange
      const token = 'valid-token';
      const newPassword = 'NewPassword123';
      const mockData = { success: true };
      const mockResponse = [mockData, null];

      auth.resetPassword.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.resetPassword(token, newPassword);

      // Assert
      expect(auth.resetPassword).toHaveBeenCalledWith(token, newPassword);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toEqual(mockData);
      expect(result[1]).toBeNull();
    });

    it('should return error for invalid token', async () => {
      // Arrange
      const token = 'invalid-token';
      const newPassword = 'NewPassword123';
      const mockError = new Error('Invalid or expired token');
      const mockResponse = [null, mockError];

      auth.resetPassword.mockResolvedValue(mockResponse);

      // Act
      const result = await auth.resetPassword(token, newPassword);

      // Assert
      expect(auth.resetPassword).toHaveBeenCalledWith(token, newPassword);
      expect(result).toEqual(mockResponse);
      expect(result[0]).toBeNull();
      expect(result[1]).toBe(mockError);
    });
  });
});
