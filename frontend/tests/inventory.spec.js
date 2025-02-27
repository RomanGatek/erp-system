import { useInventoryStore } from '../src/stores/inventory';
import { user as api } from '../src/services/api';
import { vi } from 'vitest';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

vi.mock('../src/services/api');

const app = createApp({});
const pinia = createPinia();
app.use(pinia);

describe('Inventory Store', () => {
  let store;

  beforeEach(() => {
    store = useInventoryStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch items', async () => {
    const itemsData = [{ id: 1, productName: 'Item 1' }];
    api.get.mockResolvedValueOnce({ data: itemsData });

    await store.fetchItems();
    expect(store.items).toEqual(itemsData);
  });

  it('should add an item', async () => {
    const newItem = { productName: 'New Item' };
    api.post.mockResolvedValueOnce({});
    api.get.mockResolvedValueOnce({ data: [newItem] });

    await store.addItem(newItem);
    expect(api.post).toHaveBeenCalledWith('/inventory', newItem);
    expect(store.items).toEqual([newItem]);
  });
}); 