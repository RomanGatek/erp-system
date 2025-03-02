import { defineStore } from 'pinia';
import { user as api } from '@/services/api'
import { notify } from '@kyvg/vue3-notification'

export const useProductsStore = defineStore('products', {
    state: () => ({
        products: [],
        loading: false,
        error: null,
        pagination: {
            currentPage: 1,
            perPage: 10
        },
        sorting: {
            field: 'name',
            direction: 'asc'
        },
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

            // Sort
            if (state.sorting.field !== 'actions') {
                filtered.sort((a, b) => {
                    const aVal = a[state.sorting.field];
                    const bVal = b[state.sorting.field];
                    const direction = state.sorting.direction === 'asc' ? 1 : -1;
                    return aVal > bVal ? direction : -direction;
                });
            }

            return filtered;
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
                notify({
                    type: 'success',
                    text: 'Product was successfully added',
                    duration: 5000,
                    speed: 500
                });
            } catch (err) {
                console.error(err);
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
                notify({
                    type: 'success',
                    text: 'Product was successfully updated',
                    duration: 5000,
                    speed: 500
                });
                this.error = null;
            } catch (err) {
                this.error = err;
            }
        },
        async deleteProduct(productId) {
            try {
                await api.delete(`/products/${productId}`);
                await this.fetchProducts();
                notify({
                    type: 'success',
                    text: 'Product was successfully deleted',
                    duration: 5000,
                    speed: 500
                });
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
