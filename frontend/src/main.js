import { createApp } from 'vue/dist/vue.esm-bundler';
import { createPinia } from 'pinia'
import Notifications from '@kyvg/vue3-notification'

import App from './App.vue'
import router from './router'
import './assets/tailwind.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(Notifications)

app.mount('#app')
