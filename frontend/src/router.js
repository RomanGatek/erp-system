import { createRouter, createWebHistory } from 'vue-router'
import Users from '@/views/{pages}/Users.vue'
import Products from '@/views/{pages}/Products.vue'
import Storage from '@/views/{pages}/Storage.vue'
import Home from '@/views/{application}/Home.vue'
import Auth from '@/views/{user}/Auth.vue'
import { useMeStore } from '@/stores/me.store.js'
import NotFound from '@/views/{application}/NotFound.vue'
import Profile from '@/views/{user}/Profile.vue'
import Unauthorized from '@/views/{application}/Unauthorized.vue'
import { useNotifier } from '@/stores/notifier.store.js'
import { useErrorStore } from '@/stores/errors.store.js'
import Workflow from '@/views/{pages}/Workflow.vue'
import Orders from '@/views/{pages}/Orders.vue'
import Categories from './views/{pages}/Categories.vue'
import Reports from './views/{pages}/Reports.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/auth', component: Auth, meta: { requiresAuth: false } },
  { path: '/profile', component: Profile, meta: { requiresAuth: false } },
  { path: '/users', component: Users, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/products', component: Products, meta: { requiresAuth: true, role: 'MANAGER' } },
  { path: '/storage', component: Storage, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/workflow', component: Workflow, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/reports', component: Reports, meta: { requiresAuth: true, role: 'MANAGER' } },
  { path: '/orders', component: Orders, meta: { requiresAuth: true, role: 'MANAGER' } },
  {
    path: '/products/categories',
    component: Categories,
    meta: { requiresAuth: true, role: 'MANAGER' },
  },
  { path: '/unauthorized', name: 'Unauthorized', component: Unauthorized },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const me = useMeStore()
  const e = useErrorStore()
  const notifier = useNotifier()

  if (to.path === '/auth' && token) return next('/')
  if (to.path === '/auth') return next()
  if (!token) return next('/auth')

  e.clearServerErrors()
  await me.fetchMe()

  if (me.error || !me.user) {
    notifier.error('Our session is over. Please login again', 'Session issue', 2500, 1000)
    me.logout()
    return next('/auth')
  }

  const userRoles = Object.values(me.user?.roles ?? []).map((role) => role.name)

  if (
    to.meta.requiresAuth &&
    !userRoles.some((role) => role === `ROLE_${(to.meta.role ?? '').toUpperCase()}`)
  ) {
    return next('/unauthorized')
  }
  next()
})

export default router
