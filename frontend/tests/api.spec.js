import { user, api } from '../src/services/api';
import { vi } from 'vitest';
import { createApp } from 'vue'
import { createPinia } from 'pinia'

// // Mock the axios instance
// vi.mock('axios', () => {
//   const mockAxios = {
//     create: vi.fn(() => mockAxios),
//     interceptors: {
//       request: {
//         use: vi.fn(),
//       },
//     },
//     defaults: {},
//   };
//   return mockAxios;
// });

// vi.mock('user', () => {
//   const mockUser = {
//     create: vi.fn(() => mockUser),
//     interceptors: { request: { use: vi.fn() }},
//     defaults: {}
//   }
// })

const app = createApp({})
const pinia = createPinia()
app.use(pinia)

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
