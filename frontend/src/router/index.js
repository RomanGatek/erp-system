import { createRouter, createWebHistory } from 'vue-router'
import Users from '@/views/Users.vue'
import Products from '@/views/Products.vue'
import Inventory from '@/components/Inventory.vue'
import Home from '@/views/Home.vue'
import Auth from '@/views/Auth.vue'
import { useMeStore } from '@/stores/me'
import { notify } from '@kyvg/vue3-notification'

const routes = [
  { path: '/', component: Home },
  { path: '/auth', component: Auth },
  { path: '/users', component: Users },
  { path: '/products', component: Products },
  { path: '/inventory', component: Inventory },
]


const index = createRouter({
  history: createWebHistory(),
  routes,
})


index.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const meStore = useMeStore()

  if (!token && to.path !== '/auth') {
    return next('/auth')
  }

  if (!meStore.user) {
    try {
      await meStore.fetchMe(token)
      console.log("authenticated", meStore.user)
    } catch (error) {
      notify({
        type: 'error',
        text: 'Vaše session vypršela. Prosím, přihlaste se znovu.',
        duration: 5000,
        speed: 1000
      })
      localStorage.removeItem('token')
      meStore.clearUser()
      return next('/auth')
    }
  }

  // Kontrola rolí
  const userRoles = Object.values(meStore.user?.roles ?? []).map(role => role.name)

  if (to.path === '/users' && !userRoles.includes('ROLE_ADMIN')) {
    return next('/')
  }

  if ((to.path === '/products' || to.path === '/inventory') && !userRoles.includes('ROLE_MANAGER')) {
    return next('/')
  }

  next()
})

export default index
