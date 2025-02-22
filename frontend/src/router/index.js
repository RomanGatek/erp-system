import { createRouter, createWebHistory } from 'vue-router';
import Users from "@/components/Users.vue";
import Products from "@/components/Products.vue";
import Inventory from "@/components/Inventory.vue";
import Home from "@/views/Home.vue";
import Auth from "@/views/Auth.vue";

const routes = [
  { path: '/', component: Home },
  { path: '/auth', component: Auth },
  { path: '/users', component: Users },
  { path: '/products', component: Products },
  { path: '/inventory', component: Inventory }
];

const index = createRouter({
  history: createWebHistory(),
  routes
});

index.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/auth' && !token) {
    next('/auth');
  } else {
    next();
  }
});

export default index;
