import { homeIcon, usersIcon, productsIcon, inventoryIcon, ordersIcon, storageIcon, workflowIcon } from '@/components/common/rawIcons'
import { ref } from 'vue'

export const navLinks = ref([
    {
      to: '/',
      text: 'Home',
      requiredRole: 'USER',
      icon: homeIcon
    },
    {
      to: '/users',
      text: 'Users',
      requiredRole: 'ADMIN',
      icon: usersIcon
    },
    {
      to: '/workflow',
      text: 'Workflow',
      requiredRole: 'ADMIN',
      icon: workflowIcon
    },
    {
      to: '',
      text: 'Inventory',
      requiredRole: 'MANAGER',
      icon: inventoryIcon,
      dropdown: true,
      isOpen: false,
      items: [
        {
          to: '/products',
          text: 'Products',
          requiredRole: 'MANAGER',
          icon: productsIcon
        },
        {
          to: '/orders',
          text: 'Orders',
          requiredRole: 'MANAGER',
          icon: ordersIcon
        },
        {
          to: '/storage',
          text: 'Storage',
          requiredRole: 'MANAGER',
          icon: storageIcon
        }
      ],
    },
  ])
