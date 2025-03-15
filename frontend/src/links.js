import { homeIcon, usersIcon, productsIcon, inventoryIcon, ordersIcon, storageIcon, workflowIcon, productsCategoriesIcon, catalogIcon } from '@/components'
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
      to: '/catalog',
      text: 'Catalog',
      requiredRole: 'USER',
      icon: catalogIcon
    },
    {
      to: '',
      text: 'Managment',
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
          to: '/products/categories',
          text: 'Categories',
          requiredRole: 'MANAGER',
          icon: productsCategoriesIcon
        },
        {
          to: '/storage',
          text: 'Inventory',
          requiredRole: 'MANAGER',
          icon: storageIcon
        },
        {
          to: '/orders',
          text: 'Create Order',
          requiredRole: 'MANAGER',
          icon: ordersIcon
        }
      ],
    },
  ])
