import { useMeStore } from '@/stores/me.js';
import { user as api } from '../src/services/api';
import { vi } from 'vitest';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

vi.mock('../src/services/api');

const app = createApp({});
const pinia = createPinia();
app.use(pinia);

describe('Me Store', () => {
  let store;

  beforeEach(() => {
    store = useMeStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch user data', async () => {
    const userData = { id: 1, username: 'test' };
    api.get.mockResolvedValueOnce({ data: userData });

    await store.fetchMe();
    expect(store.user).toEqual(userData);
    expect(api.get).toHaveBeenCalledWith('/me');
  });

  it('should clear user data', () => {
    store.user = { id: 1, username: 'test' };
    store.clearUser();
    expect(store.user).toBeNull();
  });
});
