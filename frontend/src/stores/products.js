import { defineStore } from 'pinia';
import {user as api} from '@/services/api' // Import your axios instance
import { ref, computed } from 'vue'

export const useProductsStore = defineStore('products', () => {
    const products = ref([])
    const error = ref(null)
    const searchQuery = ref('')
    const sorting = ref({ field: 'name', direction: 'asc' })
    const pagination = ref({
        currentPage: 1,
        perPage: 10
    })

    // Filtrované produkty podle vyhledávání
    const filteredProducts = computed(() => {
        if (!searchQuery.value) return products.value
        
        return products.value.filter(product => 
            product.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
            product.price.toString().includes(searchQuery.value)
        )
    })

    // Seřazené a stránkované produkty
    const paginatedProducts = computed(() => {
        const sorted = [...filteredProducts.value].sort((a, b) => {
            const aValue = a[sorting.value.field]
            const bValue = b[sorting.value.field]
            
            if (sorting.value.direction === 'asc') {
                return aValue > bValue ? 1 : -1
            }
            return aValue < bValue ? 1 : -1
        })

        const start = (pagination.value.currentPage - 1) * pagination.value.perPage
        const end = start + pagination.value.perPage

        return sorted.slice(start, end)
    })

    // Metody
    const fetchProducts = async () => {
        try {
            const response = await api.get('/products');
            products.value = response.data;
            error.value = null;
        } catch (err) {
            error.value = err.message;
        }
    }

    const addProduct = async (product) => {
        try {
            console.log(product);
            await api.post('/products', product);
            await fetchProducts(); // Refresh the product list after adding a new product
            error.value = null;
        } catch (err) {
            error.value = err.message;
        }
    }

    const updateProduct = async (product) => {
        try {
            await api.put(`/products/${product.id}`, product);
            await fetchProducts();
            error.value = null;
        } catch (err) {
            error.value = err.message;
        }
    }

    const deleteProduct = async (productId) => {
        try {
            await api.delete(`/products/${productId}`);
            await fetchProducts();
            error.value = null;
        } catch (err) {
            error.value = err.message;
        }
    }

    const setSearch = (query) => {
        searchQuery.value = query
        pagination.value.currentPage = 1 // Reset na první stránku při vyhledávání
    }

    const setSorting = (field) => {
        if (sorting.value.field === field) {
            sorting.value.direction = sorting.value.direction === 'asc' ? 'desc' : 'asc'
        } else {
            sorting.value = { field, direction: 'asc' }
        }
    }

    const setPage = (page) => {
        pagination.value.currentPage = page
    }

    return {
        products,
        error,
        sorting,
        pagination,
        filteredProducts,
        paginatedProducts,
        fetchProducts,
        addProduct,
        updateProduct,
        deleteProduct,
        setSearch,
        setSorting,
        setPage
    }
})
