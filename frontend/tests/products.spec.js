import { useProductsStore } from '../src/stores/products';
import { user as api } from '../src/services/api';
import { vi } from 'vitest';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

vi.mock('../src/services/api');

const app = createApp({});
const pinia = createPinia();
app.use(pinia);

describe('Products Store', () => {
  let store;

  beforeEach(() => {
    store = useProductsStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch products', async () => {
    const productsData = [{ id: 1, name: 'Product 1' }];
    api.get.mockResolvedValueOnce({ data: productsData });

    await store.fetchProducts();
    expect(store.products).toEqual(productsData);
  });

  it('should add a product', async () => {
    const newProduct = { name: 'New Product' };
    api.post.mockResolvedValueOnce({});
    api.get.mockResolvedValueOnce({ data: [newProduct] });

    await store.addProduct(newProduct);
    expect(api.post).toHaveBeenCalledWith('/products', newProduct);
    expect(store.products).toEqual([newProduct]);
  });
}); 