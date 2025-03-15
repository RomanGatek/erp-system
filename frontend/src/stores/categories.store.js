import { defineStore } from "pinia";
import api from "@/services/api";
import { 
    setupSort, 
    filter, 
    paginateViaState 
} from "@/utils";

let _;

export const useCategoriesStore = defineStore("categories", {
    state: () => ({
        /**@type {import("../../types").Category[]} */
        items: [],
        loading: false,
        error: null,
        searchQuery: '',
        pagination: { currentPage: 1, perPage: 10 },
        sorting: setupSort('name'),
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
            [this.items, this.error] = await api.category().getAll();
        },
        async addCategory(category) {
            [_, this.error] = await api.category().create(category);
            if (!this.error) this.items.push(_);
        },
        async updateCategory(id, categoryData) {
            [_, this.error] = await api.category().update(id, categoryData);
            if (!this.error) {
                const index = this.items.findIndex(p => p.id === id);
                if (index === -1) return;
                this.items[index] = _;
            }
        },
        async deleteCategory(categoryId) {
            [_, this.error] = await api.category().delete(categoryId);
            if (!this.error) {
                const index = this.items.findIndex(p => p.id === categoryId);
                if (index === -1) return;
                this.items.splice(index, 1);
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