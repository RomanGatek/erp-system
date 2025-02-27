import { defineStore } from 'pinia';
import {user as api} from '@/services/api' // Import your axios instance
import { ref, computed } from 'vue'
import { notify } from '@kyvg/vue3-notification' // Změna importu

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
            await api.post('/products', product);
            await fetchProducts();
            notify({
                type: 'success',
                text: 'Produkt byl úspěšně přidán',
                duration: 5000,
                speed: 500
            });
            error.value = null;
        } catch (err) {
            error.value = err.message;
            notify({
                type: 'error',
                text: 'Chyba při přidání produktu: ' + err.message,
                duration: 5000,
                speed: 500
            });
            throw err;
        }
    }

    const updateProduct = async (id, productData) => {
        try {
            await api.put(`/products/${id}`, productData);
            
            const index = products.value.findIndex(p => p.id === id);
            if (index !== -1) {
                products.value[index] = { 
                    ...products.value[index], 
                    ...productData,
                    id
                };
            }
            
            notify({
                type: 'success',
                text: 'Produkt byl úspěšně aktualizován',
                duration: 5000,
                speed: 500
            });
            
            error.value = null;
        } catch (err) {
            error.value = err.message;
            notify({
                type: 'error',
                text: 'Chyba při aktualizaci produktu: ' + err.message,
                duration: 5000,
                speed: 500
            });
            throw err;
        }
    }

    const deleteProduct = async (productId) => {
        try {
            await api.delete(`/products/${productId}`);
            await fetchProducts();
            notify({
                type: 'success',
                text: 'Produkt byl úspěšně smazán',
                duration: 5000,
                speed: 500
            });
            error.value = null;
        } catch (err) {
            error.value = err.message;
            notify({
                type: 'error',
                text: 'Chyba při mazání produktu: ' + err.message,
                duration: 5000,
                speed: 500
            });
            throw err;
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
