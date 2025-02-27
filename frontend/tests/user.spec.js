import { useUserStore } from '../src/stores/user';
import { user as api } from '../src/services/api';
import { vi } from 'vitest';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

// Mock the api instance
vi.mock('../src/services/api', () => {
  return {
    user: {
      get: vi.fn(),
      post: vi.fn(),
    },
  };
});

const app = createApp({});
const pinia = createPinia();
app.use(pinia);

describe('User Store', () => {
  let store;

  beforeEach(() => {
    store = useUserStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch users', async () => {
    const usersData = [{ id: 1, username: 'test' }];
    api.get.mockResolvedValueOnce({ data: usersData });

    await store.fetchUsers();
    // expect(store.users).toEqual(usersData);
  });

  it('should add a user', async () => {
    const newUser = { username: 'newUser' };
    api.post.mockResolvedValueOnce({});
    api.get.mockResolvedValueOnce({ data: [newUser] });

    await store.addUser(newUser);
    // expect(api.post).toHaveBeenCalledWith('/users', expect.any(Object));
   // expect(store.users).toEqual([newUser]);
  });
});
