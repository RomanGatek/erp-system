import { vi } from 'vitest';

export const defineStore = vi.fn((id, setup) => {
  return vi.fn(() => ({
    ...typeof setup === 'function' ? setup() : setup.state ? setup.state() : {},
    ...typeof setup === 'function' ? {} : setup.getters || {},
    ...typeof setup === 'function' ? {} : setup.actions || {},
  }));
});

export const createPinia = vi.fn(() => ({
  use: vi.fn(),
}));

export const setActivePinia = vi.fn(); 