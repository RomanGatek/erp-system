<template>
  <div class="p-8 bg-gray-100 min-h-screen">
    <div class="container mx-auto">
      <h1 class="text-3xl font-bold mb-6 text-gray-800">Products</h1>

      <div v-if="products.loading" class="text-center py-4">
        <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500"></div>
        <span class="ml-2">Loading products...</span>
      </div>
      <div v-if="products.error" class="text-red-500 text-center py-4">
        {{ products.error }}
      </div>

      <div v-if="products.products.length > 0" class="bg-white shadow rounded-lg overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
              <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products.products" :key="product.id" class="border-b border-gray-200 hover:bg-gray-100">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ product.name }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ product.price }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button @click="editProduct(product)" class="text-indigo-600 hover:text-indigo-900 mr-2 focus:outline-none">Edit</button>
                <button @click="deleteProduct(product.id)" class="text-red-600 hover:text-red-900 focus:outline-none">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else-if="(!products.loading && !products.error)" class="text-center py-4">
        No products available.
      </div>

      <div class="mt-8 bg-white shadow rounded-lg p-6">
        <h2 class="text-2xl font-bold mb-4 text-gray-800">
          <span v-if="!products.editedProduct">Add Product</span>
          <span v-else>Edit Product</span>
        </h2>
        <form @submit.prevent="submitForm">
          <div class="mb-4">
            <label for="name" class="block text-gray-700 font-bold mb-2">Name</label>
            <input type="text" id="name" v-model="productForm.name" placeholder="Name" class="border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" required>
          </div>
          <div class="mb-6">
            <label for="price" class="block text-gray-700 font-bold mb-2">Price</label>
            <input type="number" id="price" v-model="productForm.price" placeholder="Price" class="border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" required>
          </div>
          <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline mr-2">
              {{ products.editedProduct ? 'Update' : 'Add' }}
            </button>
            <button v-if="products.editedProduct" @click="cancelEdit" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { useProductsStore } from '@/stores/products';
import { onMounted, reactive, computed } from 'vue';

export default {
  name: 'ProductsComponent',
  setup() {
    const products = useProductsStore();
    const productForm = reactive({ name: '', price: null });

    const submitForm = async () => {
      if (products.editedProduct) {
        await products.updateProduct({ ...products.editedProduct, ...productForm });
      } else {
        await products.addProduct(productForm);
      }
      resetForm();
    };

    const resetForm = () => {
      productForm.name = '';
      productForm.price = null;
      products.clearEditedProduct();
    };

    onMounted(async () => {
      await products.fetchProducts();
    });

    const editProduct = (product) => {
      products.setEditedProduct(product);
      productForm.name = product.name;
      productForm.price = product.price;
    };

    const deleteProduct = async (productId) => {
      if (confirm("Are you sure you want to delete this product?")) {
        await products.deleteProduct(productId);
      }
    };

    const cancelEdit = () => {
      resetForm();
    };

    return {
      products,
      productForm,
      submitForm,
      editProduct,
      deleteProduct,
      cancelEdit
    };
  },
};
</script>
