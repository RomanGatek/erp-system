import { defineStore } from 'pinia';
import { user as api } from '@/services/api'
import { sort, setupSort } from '@/utils/sorting.js'

export const useProductsStore = defineStore('products', {
    state: () => ({
        products: [],
        loading: false,
        error: null,
        pagination: {
            currentPage: 1,
            perPage: 10
        },
        sorting: setupSort('name'),
        searchQuery: '',
    }),
    getters: {
        filteredProducts: (state) => {
            let filtered = [...state.products];
            if (state.searchQuery) {
                const searchValue = state.searchQuery.toLowerCase();
                filtered = filtered.filter(product =>
                    product.name.toLowerCase().includes(searchValue) ||
                    product.price.toString().includes(searchValue)
                );
            }
            return sort(state, filtered);
        },
        paginatedProducts: (state) => {
            const start = (state.pagination.currentPage - 1) * state.pagination.perPage;
            const end = start + state.pagination.perPage;
            return state.filteredProducts.slice(start, end);
        }
    },
    actions: {
        async fetchProducts() {
            this.loading = true;
            try {
                const response = await api.get('/products');
                this.products = response.data;
                this.error = null;
            } catch (err) {
                this.error = err;
            } finally {
                this.loading = false;
            }
        },
        async addProduct(product) {
            try {
                await api.post('/products', product);
                await this.fetchProducts();
            } catch (err) {
                this.error = err;
            }
        },
        async updateProduct(id, productData) {
            try {
                await api.put(`/products/${id}`, productData);
                const index = this.products.findIndex(p => p.id === id);
                if (index !== -1) {
                    this.products[index] = {
                        ...this.products[index],
                        ...productData,
                        id
                    };
                }
            } catch (err) {
                this.error = err;
            }
        },
        async deleteProduct(productId) {
            try {
                await api.delete(`/products/${productId}`);
                await this.fetchProducts();
                this.error = null;
            } catch (err) {
                this.error = err;
            }
        },
        setSearch(query) {
            this.searchQuery = query;
            this.pagination.currentPage = 1; // Reset to the first page when searching
        },
        setSorting(field) {
            if (this.sorting.field === field) {
                this.sorting.direction = this.sorting.direction === 'asc' ? 'desc' : 'asc';
            } else {
                this.sorting.field = field;
                this.sorting.direction = 'asc';
            }
        },
        setPage(page) {
            this.pagination.currentPage = page;
        }
    }
})
