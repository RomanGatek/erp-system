import { defineStore } from 'pinia'
import { notify } from '@kyvg/vue3-notification'

export const useNotifier = defineStore('notifierStore', {
  actions: {
    /**
     * Typická "info" notifikace
     * @param {string} message
     * @param {string} [title='Info']
     * @param {number} [duration=5000] - Doba zobrazení v ms
     * @param {number} [speed=1000] - Rychlost animace v ms
     */
    info(message, title = 'Info', duration = 5000, speed = 1000) {
      notify({
        title,
        text: message,
        type: 'info',
        closeOnClick: true,
        duration,
        speed
      })
    },

    /**
     * Notifikace pro úspěch
     * @param {string} message
     * @param {string} [title='Success']
     * @param {number} [duration=5000] - Doba zobrazení v ms
     * @param {number} [speed=1000] - Rychlost animace v ms
     */
    success(message, title = 'Success', duration = 5000, speed = 1000) {
      notify({
        title,
        text: message,
        type: 'success',
        closeOnClick: true,
        duration,
        speed
      })
    },

    /**
     * Notifikace pro varování
     * @param {string} message
     * @param {string} [title='Warning']
     * @param {number} [duration=5000] - Doba zobrazení v ms
     * @param {number} [speed=1000] - Rychlost animace v ms
     */
    warning(message, title = 'Warning', duration = 5000, speed = 1000) {
      notify({
        title,
        text: message,
        type: 'warn',
        closeOnClick: true,
        duration,
        speed
      })
    },

    /**
     * Notifikace pro chybu
     * @param {string} message
     * @param {string} [title='Error']
     * @param {number} [duration=5000] - Doba zobrazení v ms
     * @param {number} [speed=1000] - Rychlost animace v ms
     */
    error(message, title = 'Error', duration = 5000, speed = 1000) {
      notify({
        title,
        text: message,
        type: 'error',
        closeOnClick: true,
        duration,
        speed
      })
    },
  },
})
