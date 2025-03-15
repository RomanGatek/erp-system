import Checkout from '@/views/checkout/Checkout.vue'

const routes = [
  // ... existing routes ...
  {
    path: '/checkout',
    name: 'Checkout',
    component: Checkout,
    meta: {
      requiresAuth: false,
      title: 'Checkout'
    }
  },
  // For order confirmation
  {
    path: '/order-confirmation',
    name: 'OrderConfirmation',
    component: () => import('@/views/checkout/OrderConfirmation.vue'),
    meta: {
      requiresAuth: false,
      title: 'Order Confirmation'
    }
  },
  // ... existing routes ...
] 