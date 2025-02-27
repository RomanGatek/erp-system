<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-500 to-purple-600">
    <div class="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
      <h1 class="text-4xl font-bold mb-6 text-center text-gray-800">{{ isLogin ? 'Login' : 'Register' }}</h1>
      <form @submit.prevent="isLogin ? login() : register()" class="space-y-6">
        <div>
          <input
            type="email"
            v-model="email"
            placeholder="Email"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>
        <div v-if="!isLogin">
          <input
            type="email"
            v-model="email"
            placeholder="Email"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>
        <div>
          <input
            type="password"
            v-model="password"
            placeholder="Password"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>
        <div v-if="!isLogin">
          <input
            type="password"
            v-model="confirmPassword"
            placeholder="Confirm Password"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-600"
          />
        </div>
        <div>
          <button
            type="submit"
            class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600"
          >
            {{ isLogin ? 'Login' : 'Register' }}
          </button>
        </div>
      </form>
      <div class="text-center mt-4">
        <button @click="toggleForm" class="text-blue-600 hover:underline">
          {{ isLogin ? 'Don\'t have an account? Register' : 'Already have an account? Login' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { login, register } from '../services/auth';
import { useMeStore } from '../stores/me';
import { notify } from '@kyvg/vue3-notification'

export default {
  name: 'LoginView',
  data() {
    return {
      isLogin: true,
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    }
  },
  methods: {
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
    async login() {
      try {
        const token = await login(this.email, this.password);
        if (!token) {
          notify({
            type: 'error',
            text: 'Vaše autorizační token není platný.',
            duration: 5000,
            speed: 500
          })
          return;
        }
        localStorage.setItem('token', token);
        const meStore = useMeStore();
        await meStore.fetchMe(token)
        this.$router.push('/');

        notify({
          type: 'success',
          text: 'Úspěšně jste se přihlásil.',
          duration: 2000,
          speed: 500
        })

      } catch (error) {
        notify({
          type: 'error',
          text: 'Nastala chyba při přihlášení. Chyba: ' + error.response.message || error.message,
          duration: 5000,
          speed: 500
        })
        console.error('Login failed', error);
      }
    },
    async register() {
      if (this.password !== this.confirmPassword) {
        notify({
          type: 'error',
          text: 'Hesla se neshodují. Prosím zkuste to znovu.',
          duration: 5000,
          speed: 500
        })
        return;
      }
      try {
        await register(this.username, this.email, this.password);
        notify({
          type: 'success',
          text: 'Úspěšně jste se zaregistroval.',
          duration: 5000,
          speed: 500
        })
        this.isLogin = true;
      } catch (error) {
        console.error('Registration failed', error);
        notify({
          type: 'error',
          text: 'Nastala chyba při registraci. Chyba: ' + error.response.message || error.message,
          duration: 5000,
          speed: 500
        })
      }
    }
  }
}
</script>
