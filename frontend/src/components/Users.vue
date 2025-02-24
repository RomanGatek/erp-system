
<template>
  <div class="p-8">
    <h1 class="text-3xl font-bold mb-6">Users</h1>

    <div v-if="userStore.loading">Loading users...</div>
    <div v-if="userStore.error" class="text-red-500">{{ userStore.error }}</div>

    <table v-if="userStore.users.length" class="min-w-full divide-y divide-gray-200">
      <thead>
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(user, index) in userStore.users" :key="user.id" class="border-b border-gray-200 hover:bg-gray-50">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ user.name }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ user.email }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <button @click="editUser(index)" class="text-indigo-600 hover:text-indigo-900 mr-2">Edit</button>
            <button @click="deleteUser(user.id)" class="text-red-600 hover:text-red-900">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-else-if="!userStore.loading && !userStore.error">No users available.</p>

    <div class="mt-8">
      <h2 class="text-2xl font-bold mb-4">{{ isEditing ? 'Edit User' : 'Add User' }}</h2>
      <form @submit.prevent="isEditing ? updateUser() : addUser()">
        <div class="grid grid-cols-2 gap-4">
          <input type="text" v-model="userForm.name" placeholder="Name" class="border rounded p-2" />
          <input type="email" v-model="userForm.email" placeholder="Email" class="border rounded p-2" />
        </div>
        <div class="mt-4">
          <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mr-2">
            {{ isEditing ? 'Update' : 'Add' }}
          </button>
          <button v-if="isEditing" type="button" @click="cancelEdit" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

const newUser = reactive({
  name: '',
  email: '',
});

const isEditing = ref(false);
const editingUser = reactive({
  name: '',
  email: ''
});
const editedUserIndex = ref(null);

const userForm = computed(() => (isEditing.value ? editingUser : newUser));

onMounted(async () => {
  await userStore.fetchUsers();
});

const addUser = async () => {
  await userStore.addUser({ ...newUser });
  Object.assign(newUser, { name: '', email: '' });
};

const editUser = (index) => {
  isEditing.value = true;
  editedUserIndex.value = index;
  Object.assign(editingUser, userStore.users[index]);
};

const updateUser = async () => {
  await userStore.updateUser(editingUser);
  isEditing.value = false;
  editedUserIndex.value = null;
  Object.assign(editingUser, { name: '', email: '' });
};

const cancelEdit = () => {
  isEditing.value = false;
  editedUserIndex.value = null;
  Object.assign(editingUser, { name: '', email: '' });
};

const deleteUser = async (userId) => {
  if (confirm('Are you sure you want to delete this user?')) {
    await userStore.deleteUser(userId);
  }
};
</script>

