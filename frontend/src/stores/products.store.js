import { defineStore } from 'pinia';
import api from '@/services/api'
import {
  filter,
  setupSort
} from '@/utils'
import { paginateViaState } from '@/utils'

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
        paginateItems: (state) => paginateViaState(state)
    },
    actions: {
        async fetchProducts() {
            this.loading = true;
            [this.items, this.error] = await api.products().getAll();
            this.loading = false;
        },
        async addProduct(product) {
            var _;
            [_, this.error] = await api.products().add({ ...product, productCategory: product.productCategory?.name });
            if (!this.error) await this.fetchProducts();
        },
        async updateProduct(id, productData) {
            var item;
            [item, this.error] = await api.products().update(id, productData);
            if (!this.error) await this.fetchProducts();
            if (item) this.items[this.items.findIndex((item) => item.id === id)] = item;
        },
        async deleteProduct(productId) {
            var _;
            [_, this.error] = await api.products().delete(productId);
            await this.fetchProducts();
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
