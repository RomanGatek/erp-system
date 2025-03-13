import { createApp } from 'vue/dist/vue.esm-bundler';
import { createPinia } from 'pinia'
import Notifications from '@kyvg/vue3-notification'

import App from './App.vue'
import router from './router.js'
import './assets/tailwind.css'
import './assets/main.css'

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(Notifications)

app.mount('#app')