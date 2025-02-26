<template>
  <div class="p-8 space-y-6">
    <div class="bg-white p-6 rounded-2xl shadow-lg ring-1 ring-gray-200">
      <h2 class="text-2xl font-bold text-gray-800 mb-4">Inventory</h2>
      <table
        v-if="inventory.items.length"
        class="w-full border-collapse shadow-md rounded-lg overflow-hidden"
      >
        <thead>
          <tr class="bg-gradient-to-r from-blue-500 to-indigo-500 text-white">
            <th class="py-3 px-6 text-left">Product Name</th>
            <th class="py-3 px-6 text-left">Quantity</th>
            <th class="py-3 px-6 text-left">Location</th>
            <th class="py-3 px-6 text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in inventory.items"
            :key="item.id"
            class="border-t hover:bg-gray-100 transition"
            @click="openItemModal(index)"
          >
            <td class="py-3 px-6 cursor-pointer text-gray-700 font-medium">
              {{ item.productName }}
            </td>
            <td class="py-3 px-6 text-gray-600">{{ item.quantity }}</td>
            <td class="py-3 px-6 text-gray-600">{{ item.location }}</td>
            <td class="py-3 px-6 text-right">
              <button
                class="bg-red-500 hover:bg-red-600 text-white px-4 py-1 rounded-lg shadow-md transition"
                @click.stop="deleteItem(item.id)"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal pro editaci -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center backdrop-blur-sm"
    >
      <div class="bg-white p-8 rounded-2xl shadow-xl w-96 ring-1 ring-gray-200">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">Edit Inventory Item</h2>
        <input
          v-model="selectedItem.productName"
          type="text"
          placeholder="Product Name"
          class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 mb-2"
        />
        <input
          v-model="selectedItem.quantity"
          type="number"
          placeholder="Quantity"
          class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 mb-2"
        />
        <input
          v-model="selectedItem.location"
          type="text"
          placeholder="Location"
          class="w-full p-3 border rounded-lg shadow-sm focus:ring-2 focus:ring-green-500 mb-4"
        />
        <div class="flex justify-between">
          <button
            class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-lg shadow-md transition"
            @click="updateItem"
          >
            Update
          </button>
          <button
            class="bg-gray-500 hover:bg-gray-600 text-white px-6 py-2 rounded-lg shadow-md transition"
            @click="cancelEdit"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useInventoryStore } from '@/stores/inventory'

const inventory = useInventoryStore()
const isModalOpen = ref(false)
const selectedItem = reactive({ productName: '', quantity: 0, location: '' })
const editedItemIndex = ref(null)

onMounted(async () => {
  await inventory.fetchItems()
})

const openItemModal = (index) => {
  editedItemIndex.value = index
  Object.assign(selectedItem, inventory.items[index])
  isModalOpen.value = true
}

const updateItem = async () => {
  await inventory.updateItem(selectedItem)
  isModalOpen.value = false
  editedItemIndex.value = null
}

const cancelEdit = () => {
  isModalOpen.value = false
  editedItemIndex.value = null
}

const deleteItem = async (itemId) => {
  if (confirm('Are you sure you want to delete this item?')) {
    await inventory.deleteItem(itemId)
  }
}
</script>
