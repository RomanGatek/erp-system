import { defineStore } from 'pinia';
import {user as api} from '@/services/api' // Import your axios instance
export const useProductsStore = defineStore('products', {
    state: () => ({
        products: [],
        loading: false,
        error: null,
        editedProduct: null // Pro editaci produktu
    }),
    getters: {
        availableProducts: (state) => state?.products.filter(product => product?.available),
    },
    actions: {
        async fetchProducts() {
            this.loading = true;
            try {
                const response = await api.get('/products');
                this.products = response.data;
            } catch (error) {
                this.error = error.response.data.message || error.message;
            } finally {
                this.loading = false;
            }
        },
        async addProduct(product) {
            try {
                await api.post('/products', product);
                await this.fetchProducts(); // Refresh the product list after adding a new product
            } catch (error) {
                this.error = error.response.data.message || error.message;
            }
        },
        async updateProduct(product) {
            try {
                await api.put(`/products/${product.id}`, product);
                await this.fetchProducts();
                this.editedProduct = null;
            } catch (error) {
                this.error = error.response.data.message || error.message;
            }
        },
        async deleteProduct(productId) {
            try {
                await api.delete(`/products/${productId}`);
                await this.fetchProducts();
            } catch (error) {
                this.error = error.response.data.message || error.message;
            }
        },
        setEditedProduct(product) {
            this.editedProduct = product;
        },
        clearEditedProduct() {
            this.editedProduct = null;
        }
    },
});
