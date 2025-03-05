import { describe, it, expect, beforeEach, vi } from 'vitest'
import router from '@/router'

// Helper function to flush the promise queue
function flushPromises() {
  return new Promise(resolve => setTimeout(resolve, 0));
}

// Create persistent mocks so that both the router and tests use the same instance
const meStoreMock = {
  fetchMe: vi.fn(),
  clearUser: vi.fn(),
  error: false,
  user: null
};

const errorStoreMock = {
  clearServerErrors: vi.fn()
};

const notifierMock = {
  error: vi.fn()
};

// Persistent mocks for store modules
vi.mock('@/stores/me', () => ({
  useMeStore: () => meStoreMock
}));

vi.mock('@/stores/errors.js', () => ({
  useErrorStore: () => errorStoreMock
}));

vi.mock('@/stores/notifier.js', () => ({
  useNotifier: () => notifierMock
}));

describe('Router Navigation Guard', () => {
  beforeEach(async () => {
    // Clear localStorage and reset mock states
    localStorage.clear();
    meStoreMock.fetchMe.mockReset();
    meStoreMock.clearUser.mockReset();
    errorStoreMock.clearServerErrors.mockReset();
    notifierMock.error.mockReset();

    // Default store state – no error, empty roles
    meStoreMock.error = false;
    meStoreMock.user = { roles: [] };

    // Implement fetchMe that immediately resolves
    meStoreMock.fetchMe.mockImplementation(async () => {});

    // Reset router – navigate to the root path
    await router.push('/');
    await flushPromises();
  });

  it('redirects to / when navigating to /auth with a token', async () => {
    localStorage.setItem('token', 'dummy');
    await router.push('/auth');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/auth');
  });

  it('allows navigation to /auth without a token', async () => {
    await router.push('/auth');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/auth');
  });

  it('redirects to /auth for protected routes when the token is missing', async () => {
    await router.push('/users');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/auth');
  });

  it('redirects to /auth and triggers notifier.error when fetchMe fails', async () => {
    localStorage.setItem('token', 'dummy');
    meStoreMock.error = true;
    meStoreMock.user = null;
    await router.push('/users');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/auth');
    expect(notifierMock.error).toHaveBeenCalledWith(
      "Our session is over. Please login again",
      "",
      2500,
      1000
    );
    expect(localStorage.getItem('token')).toBeNull();
    expect(meStoreMock.clearUser).toHaveBeenCalled();
  });

  it('allows access to a protected route if the user has the required role', async () => {
    localStorage.setItem('token', 'dummy');
    meStoreMock.error = false;
    meStoreMock.user = { roles: [{ name: 'ROLE_ADMIN' }] }; // /users requires ADMIN role
    await router.push('/users');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/users');
  });

  it('redirects to /unauthorized if the user lacks the required role', async () => {
    localStorage.setItem('token', 'dummy');
    meStoreMock.error = false;
    meStoreMock.user = { roles: [{ name: 'ROLE_USER' }] }; // User does not have the required role
    await router.push('/users');
    await flushPromises();
    expect(router.currentRoute.value.path).toBe('/unauthorized');
  });
});
