import { createRouter, createWebHistory } from 'vue-router'
import Users from '@/views/Users.vue'
import Products from '@/views/Products.vue'
import Inventory from '@/views/Inventory.vue'
import Home from '@/views/Home.vue'
import Auth from '@/views/Auth.vue'
import { useMeStore } from '@/stores/me'
import { notify } from '@kyvg/vue3-notification'
import NotFound from '@/views/NotFound.vue'
import Profile from '@/views/Profile.vue'
import Unauthorized from '@/views/Unauthorized.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/auth', component: Auth },
  { 
    path: '/profile', 
    component: Profile,
    meta: { requiresAuth: true }
  },
  { path: '/users', component: Users },
  { path: '/products', component: Products },
  { path: '/inventory', component: Inventory },
  {
    path: '/unauthorized',
    name: 'Unauthorized',
    component: Unauthorized
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const meStore = useMeStore()

  if (to.meta.requiresAuth && !token) {
    return next('/unauthorized')
  }

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

  const userRoles = Object.values(meStore.user?.roles ?? []).map(role => role.name)

  if (to.path === '/users' && !userRoles.includes('ROLE_ADMIN')) {
    return next('/')
  }

  if ((to.path === '/products' || to.path === '/inventory') && !userRoles.includes('ROLE_MANAGER')) {
    return next('/')
  }

  next()
})

export default router
