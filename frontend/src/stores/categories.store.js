import { defineStore } from "pinia";
import api from "@/services/api";
import { setupSort, filter, paginateViaState } from "@/utils";

export const useCategoriesStore = defineStore("categories", {
    state: () => ({
        /**@type {import("../../types").Category[]} */
        items: [],
        loading: false,
        error: null,
        pagination: { currentPage: 1, perPage: 10 },
        sorting: setupSort('name'),
        searchQuery: '',
    }),
    getters: {
        filtered: (state) => filter(state, (/**@type {import("../../types").Category} */category) => {
            const search = state.searchQuery.toLocaleLowerCase();
            return category.name.toLowerCase().includes(search) ||
            category.description.toString().includes(search)
        }),
        paginateItems: (state) => paginateViaState(state)
    },
    actions: {
        async fetchCategories() {
            this.loading = true;
            [this.items, this.error] = await api.category().getAll();
            this.loading = false;
        },
        async addCategory(category) {
            this.loading = true;
            var output;
            [output, this.error] = await api.category().create(category);
            await this.fetchCategories();
            this.loading = false;
        },
        async updateCategory(id, categoryData) {
            console.log(1, id, categoryData);
            this.loading = true;
            var output;
            [output, this.error] = await api.category().update(id, categoryData);
            await this.fetchCategories();
            this.loading = false;
        },
        async deleteCategory(categoryId) {
            this.loading = true;
            var output;
            [output, this.error] = await api.category().delete(categoryId);
            if (!this.error) {
                await this.fetchCategories();
                this.loading = false;
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
});