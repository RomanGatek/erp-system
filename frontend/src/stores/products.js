import { defineStore } from 'pinia';
import { api } from '@/services/api'
import {
  filter,
  setupSort
} from '@/utils/table-utils.js'
import { __paginate } from '@/utils/pagination.js'

export const useProductsStore = defineStore('products', {
    state: () => ({
        items: [],
        loading: false,
        error: null,
        pagination: { currentPage: 1, perPage: 10 },
        sorting: setupSort('name'),
        searchQuery: '',
    }),
    getters: {
        filtered: (state) => filter(state, (product) => {
            const search = state.searchQuery.toLocaleLowerCase();
            return product.name.toLowerCase().includes(search) ||
            product.buyoutPrice.toString().includes(search) || product.purchasePrice.toString().includes(search)
        }),
        paginateItems: (state) => __paginate(state)
    },
    actions: {
        async fetchProducts() {
            this.loading = true;
            try {
                const response = await api.get('/products');

                console.log(response.data)

                this.items = response.data;
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
                this.error = null;
            } catch (err) {
                this.error = err;
            }
        },
        async updateProduct(id, productData) {
            try {
                await api.put(`/products/${id}`, productData);
                const index = this.items.findIndex(p => p.id === id);
                if (index !== -1) {
                    this.items[index] = {
                        ...this.items[index],
                        ...productData,
                        id
                    };
                }
                this.error = null;
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
