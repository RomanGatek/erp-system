import { createRouter, createWebHistory } from 'vue-router'
import Users from '@/views/Users.vue'
import Products from '@/views/Products.vue'
import Storage from '@/views/Storage.vue'
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
  { path: '/users', component: Users, meta: { requiresAuth: true }   },
  { path: '/products', component: Products, meta: { requiresAuth: true } },
  { path: '/storage', component: Storage, meta: { requiresAuth: true } },
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

  if (to.path === '/auth' && token) return next('/')
  if (to.path === '/auth') return next()
  if (!token) return next('/auth')

  if (!meStore.user) {
    await meStore.fetchMe()
    if (meStore.error) {
      notify({
        type: 'error',
        text: 'Our session is over. Please login again.',
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
    return next('/unauthorized')
  }

  if ((to.path === '/products' || to.path === '/inventory') && !userRoles.includes('ROLE_MANAGER')) {
    return next('/unauthorized')
  }

  next()
})

export default router
