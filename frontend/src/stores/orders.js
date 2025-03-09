import { defineStore } from 'pinia';

export const useOrdersStore = defineStore('orders', {
    state: () => ({
        order: null,
    }),
    actions: {
      loadOrderForEdit(orderFromDb)  {
        console.log(orderFromDb);
        this.order = orderFromDb;
      }
    }
})
