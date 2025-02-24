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
import Swal from 'sweetalert2';

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
          await Swal.fire({
            icon: 'error',
            title: 'Login failed',
            text: 'Your authorization token are not valid.'
          });
          return;
        }
        Swal.fire({
          icon: 'success',
          title: 'Login Successful',
          text: 'You have been successfully logged in!',
        }).then((after) => {
            if (after.isConfirmed) {
              console.log("TOKEN: " + token);
              localStorage.setItem('token', token);
              this.$router.push('/');
            }
        });
      } catch (error) {
        console.error('Login failed', error);
        await Swal.fire({
          icon: 'error',
          title: 'Login Failed',
          text: 'Invalid username or password. Please try again.',
        });
      }
    },
    async register() {
      if (this.password !== this.confirmPassword) {
        await Swal.fire({
          icon: 'error',
          title: 'Registration Failed',
          text: 'Passwords do not match. Please try again.',
        });
        return;
      }
      try {
        await register(this.username, this.email, this.password);
        await Swal.fire({
          icon: 'success',
          title: 'Registration Successful',
          text: 'You have been successfully registered!',
        });
        this.isLogin = true;
      } catch (error) {
        console.error('Registration failed', error);
        await Swal.fire({
          icon: 'error',
          title: 'Registration Failed',
          text: 'An error occurred during registration. Please try again.',
        });
      }
    }
  }
}
</script>
