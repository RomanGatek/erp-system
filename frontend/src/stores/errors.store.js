import { defineStore } from 'pinia'
import { useNotifier } from './notifier.store'
import { errorParser } from '@/utils/errors-utils'

export const useErrorStore = defineStore('errors', {
  state: () => ({
    /**
     * Výchozí struktura chyb, do které
     * při inicializaci store vracíme stav.
     */
    errorDefault: [],
    general: null,

    /**
     * Pokud potřebujete v clearerrorStore.errors
     * mazat nějakou další chybu mimo `server`,
     * je možné mít i referenci na nějaký externí store nebo objekt.
     */
    externalStore: null
  }),

  actions: {
    /**
     * Umožňuje upravit základní podobu (default) chyb,
     * pokud byste chtěli třeba přidat další klíč(e).
     * @param {string[]} object 
     */
    validateField(object) {
      this.errorDefault = Array.isArray(object) ? object : Object.keys(object)
    },

    clear(field) {
      delete this[field]
    },
    /**
     * Nastaví odkaz na externí store nebo objekt,
     * u kterého chcete např. v `clearerrorStore.errors` nastavovat error = null.
     */
    setExternalStore(store) {
      this.externalStore = store
    },

    /**
     * Vymaže stávající chyby ve `server` a případně i v `externalStore.error`.
     */
    clearServerErrors() {
      if (this.externalStore && this.externalStore.error !== undefined) {
        this.externalStore.error = null
      }
      
      // Clear all error fields except the predefined properties
      const keysToPreserve = ['errorDefault', 'externalStore', 'general']
      Object.keys(this).forEach(key => {
        if (!keysToPreserve.includes(key) && typeof this[key] !== 'function') {
          delete this[key]
        }
        delete this['general']
      })
      
      // Reset general error to null
      this.general = null
    },

    /**
     * Pomocná metoda pro vymazání všech chyb
     */
    $clear() {
      // Clear all error fields except the predefined properties
      const keysToPreserve = ['errorDefault', 'externalStore']
      Object.keys(this).forEach(key => {
        if (!keysToPreserve.includes(key) && typeof this[key] !== 'function') {
          delete this[key]
        }
      })
      
      // Reset general error to null
      this.general = null
    },

    /**
     * Vrátí objekt obsahující pouze chybové pole,
     * bez defaultních properties store
     */
    fields() {
      const result = {}
      const fieldsToExclude = [
        'errorDefault', 
        'externalStore',
        // Pinia internal properties and methods
        '$id',
        '$state',
        '$reset',
        '$patch',
        '$subscribe',
        '$dispose'
      ]
      
      Object.keys(this).forEach(key => {
        // Exclude functions and default fields
        if (!fieldsToExclude.includes(key) && 
            typeof this[key] !== 'function' && 
            this[key] !== null) {
          result[key] = this[key]
        }
      })
      
      // Explicitly add general error if it exists
      if (this.general !== null) {
        result.general = this.general
      }
      
      return result
    },

    /**
     * Hlavní metoda pro zpracování chyb, kterou můžete volat:
     *    errors.handle(error, nějakýRef);
     *
     * - `error`  – obvykle zachycená chyba z try/catch.
     * @param {import('@/utils/errors-utils').RawError} error 
     */
    handle(error) {
      const $notifier = useNotifier()
      this.$clear()
      const parsed = errorParser(error)
      try {
        parsed.errors.forEach((/**@type {import('@/utils/errors-utils').ErrorItem}*/ e) => {
          const contains = [...this.errorDefault].includes(e.field)
          if (contains) {
            this[e.field] = e.message
          } else {
            this['general'] = e.message
          }
        });
      } catch (e) {
        $notifier.error('An unexpected error occurred', e.message, 2000)
        console.error(error)
      }
    }
  }
})