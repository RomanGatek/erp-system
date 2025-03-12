if (typeof global === 'undefined') {
    window.global = window;
}

import { createApp } from 'vue/dist/vue.esm-bundler';
import { createPinia } from 'pinia'
import Notifications from '@kyvg/vue3-notification'

import App from './App.vue'
import router from './router.js'
import './assets/tailwind.css'
import './assets/main.css'
import { setupWebSocket } from '@/services/socket'

// Vytvoření Pinia store
const pinia = createPinia()

// Vytvoření Vue aplikace
const app = createApp(App)
app.use(pinia)
setupWebSocket(app)

app.use(router)
app.use(Notifications)
app.mount('#app')
window.$vue = app

window.addEventListener('error', (event) => {
  console.error('Uncaught error:', event.error)
})

// Globální error handler pro nezachycené promise rejections
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled promise rejection:', event.reason)
})


