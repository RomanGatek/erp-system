import { auth } from '@/services/auth.js';
import { api } from '@/services/api.js';
import { vi } from 'vitest';

vi.mock('../src/services/api');

describe('Auth Service', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should login user', async () => {
    const credentials = { username: 'test', password: 'test' };
    const responseData = { token: 'test-token' };
    api.post.mockResolvedValueOnce({ data: responseData });

    const result = await auth.login(credentials);
    expect(result).toEqual(null);
    expect(api.post).toHaveBeenCalledWith('/auth/public/login', JSON.stringify(credentials));
  });

  it('should register user', async () => {
    const userData = { username: 'test', password: 'test' };
    const responseData = { success: true };
    api.post.mockResolvedValueOnce({ data: responseData });

    const result = await auth.register(userData);
    expect(result).toEqual(responseData);
    expect(api.post).toHaveBeenCalledWith('/auth/public/signup', userData);
  });

  it('should logout user', async () => {
    const responseData = { success: true };
    api.post.mockResolvedValueOnce({ data: responseData });

    const result = await auth.logout();
    expect(result).toEqual(responseData);
    expect(api.post).toHaveBeenCalledWith('/auth/public/logout');
  });
});
