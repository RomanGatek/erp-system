import { api } from '@/services/api.js'
import {
  afterEach,
  describe,
  it,
  expect,
  vi
} from 'vitest'


describe('API service', async () => {
  afterEach(() => vi.clearAllMocks())
});


/*
describe('API Service', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should create a user-specific axios instance', () => {
    expect(user.defaults.baseURL).toBe('http://localhost:8080/api');
    expect(user.defaults.headers['Content-Type']).toBe('application/json');
  });

  it('should add token to request headers', async () => {
    const token = 'test-token';
    localStorage.setItem('token', token);

    const config = { headers: {} };
    await user.interceptors.request?.handlers[0].fulfilled(config);

    expect(config.headers.Authorization).toBe(`Bearer ${token}`);
  });

  it('should remove Content-Type for FormData', async () => {
    const config = {
      data: new FormData(),
      headers: { 'Content-Type': 'application/json' }
    };
    await user.interceptors.request.handlers[0].fulfilled(config);

    expect(config.headers['Content-Type']).toBeUndefined();
  });
});
*/
