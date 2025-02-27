import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {user as api} from '@/services/api' // Import your axios instance

export const useInventoryStore = defineStore('inventory', () => {
  const items = ref([])
  const error = ref(null)
  const searchQuery = ref('')
  const sorting = ref({ field: 'productName', direction: 'asc' })
  const pagination = ref({
    currentPage: 1,
    perPage: 10
  })

  // Filtrované položky podle vyhledávání
  const filteredItems = computed(() => {
    if (!searchQuery.value) return items.value
    
    return items.value.filter(item => 
      item.productName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.quantity.toString().includes(searchQuery.value) ||
      item.location.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  })

  // Seřazené a stránkované položky
  const paginatedItems = computed(() => {
    const sorted = [...filteredItems.value].sort((a, b) => {
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
  const fetchItems = async () => {
    try {
      const response = await api.get('/inventory')
      items.value = response.data
      error.value = null
    } catch (err) {
      error.value = err.response?.data?.message || err.message
    }
  }

  const addItem = async (item) => {
    try {
      await api.post('/inventory', item)
      await fetchItems()
    } catch (err) {
      error.value = err.response?.data?.message || err.message
    }
  }

  const updateItem = async (item) => {
    try {
      await api.put(`/inventory/${item.id}`, item)
      await fetchItems()
    } catch (err) {
      error.value = err.response?.data?.message || err.message
    }
  }

  const deleteItem = async (itemId) => {
    try {
      await api.delete(`/inventory/${itemId}`)
      await fetchItems()
    } catch (err) {
      error.value = err.response?.data?.message || err.message
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
    items,
    error,
    sorting,
    pagination,
    filteredItems,
    paginatedItems,
    fetchItems,
    addItem,
    updateItem,
    deleteItem,
    setSearch,
    setSorting,
    setPage
  }
})
