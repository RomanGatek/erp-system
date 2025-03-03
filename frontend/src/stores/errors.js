import { defineStore } from 'pinia'
import { AxiosError } from 'axios'
import { notify } from '@kyvg/vue3-notification'
import { ref } from 'vue'

export const useErrorStore = defineStore('errorStore', {
  state: () => ({
    /**
     * Výchozí struktura chyb, do které
     * při inicializaci store vracíme stav.
     */
    errorDefault: {
      general: '',
    },

    /**
     * Sem se ukládají (reaktivně) aktuální chyby
     * – např. `server.value.general`, `server.value.email` apod.
     */
    server: ref({ general: '' }),

    /**
     * Pokud potřebujete v clearServerErrors
     * mazat nějakou další chybu mimo `server`,
     * je možné mít i referenci na nějaký externí store nebo objekt.
     */
    externalStore: null,
  }),

  actions: {
    /**
     * Umožňuje upravit základní podobu (default) chyb,
     * pokud byste chtěli třeba přidat další klíč(e).
     */
    setErrorDefault(object) {
      this.errorDefault = { ...object, ...this.errorDefault }
    },

    /**
     * Nastaví odkaz na externí store nebo objekt,
     * u kterého chcete např. v `clearServerErrors` nastavovat error = null.
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
      this.server.value = { ...this.errorDefault }
    },

    /**
     * Hlavní metoda pro zpracování chyb, kterou můžete volat:
     *    errorStore.handle(error, nějakýRef);
     *
     * - `error`  – obvykle zachycená chyba z try/catch
     * - `srv`    – volitelný ref (resp. reactive), do kterého můžete zapsat detailní chybovou hlášku,
     *              pokud je to potřeba (např. `srv.value = "Chybová hláška"`).
     */
    handle(error, srv) {
      this.clearServerErrors()

      if (error instanceof AxiosError) {
        const rsp = error?.response?.data

        // Pokud žádná data nepřišla, zobrazíme obecnou chybu
        if (!rsp) {
          this.server.value.general = 'An unexpected error occurred'
          return
        }

        // Může jít o pole chyb { field, message } nebo o jediný objekt
        if (Array.isArray(rsp)) {
          rsp.forEach(({ field: errorField, message: errorMessage }) => {
            if (errorField) {
              this.server.value[errorField] = errorMessage
            } else {
              this.server.value.general = errorMessage
            }
          })
        } else {
          const { field: errorField, message: errorMessage } = rsp
          if (['database', 'argument'].includes(errorField)) {
            // Zvláštní zacházení s některými "typy" chyb
            this.server.value.general = errorMessage
            if (srv) srv.value = errorMessage
          } else {
            if (errorField) {
              this.server.value[errorField] = errorMessage
            } else {
              this.server.value.general = errorMessage
            }
          }
        }
      } else {
        // Pokud není AxiosError, použijeme notifikaci
        notify({
          text: error.message,
          title: 'An unexpected error occurred',
          type: 'error',
          duration: 5000,
          speed: 500
        })
        console.error(error)
      }
    },
  },
})
