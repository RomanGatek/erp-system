import { createRouter, createWebHistory } from 'vue-router'
import Users from '@/views/Users.vue'
import Products from '@/views/Products.vue'
import Storage from '@/views/Storage.vue'
import Home from '@/views/Home.vue'
import Auth from '@/views/Auth.vue'
import { useMeStore } from '@/stores/me'
import NotFound from '@/views/NotFound.vue'
import Profile from '@/views/Profile.vue'
import Unauthorized from '@/views/Unauthorized.vue'
import { useNotifier } from '@/stores/notifier.js'
import { useErrorStore } from '@/stores/errors.js'
import Workflow from '@/views/Workflow.vue'
import Orders from '@/views/Orders.vue'
import Inventory from '@/views/Inventory.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/auth', component: Auth, meta: { requiresAuth: false } },
  { path: '/profile', component: Profile, meta: { requiresAuth: false } },
  { path: '/users', component: Users, meta: { requiresAuth: true, role: "ADMIN" } },
  { path: '/products', component: Products, meta: { requiresAuth: true, role: "MANAGER" } },
  { path: '/storage', component: Storage, meta: { requiresAuth: true, role: "ADMIN" } },
  { path: '/workflow', component: Workflow, meta: { requiresAuth: true, role: "ADMIN" } },
  { path: '/orders', component: Orders, meta: { requiresAuth: true, role: "MANAGER" } },
  { path: '/stock-orders', component: Inventory, meta: { requiresAuth: true, role: "MANAGER" } },
  { path: '/unauthorized', name: 'Unauthorized', component: Unauthorized },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const me = useMeStore()
  const e = useErrorStore()
  const notifier = useNotifier();

  if (to.path === '/auth' && token) return next('/')
  if (to.path === '/auth') return next()
  if (!token) return next('/auth')

  e.clearServerErrors();
  await me.fetchMe()

  if (me.error || !me.user) {
    notifier.error("Our session is over. Please login again", "", 2500, 1000)
    localStorage.removeItem('token')
    me.clearUser()
    return next('/auth')
  }

  const userRoles = Object.values(me.user?.roles ?? []).map(role => role.name)

  if (to.meta.requiresAuth && !userRoles.some(role => role === `ROLE_${(to.meta.role ?? "").toUpperCase()}`)) {
    return next('/unauthorized')
  }
  next()
})

export default router
