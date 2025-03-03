import { AxiosError } from 'axios'
import { notify } from '@kyvg/vue3-notification'

let errorDefault = {
  general: ''
}
let server;
let store_;

export const setErrorDefault = (object) => {
  errorDefault = {...object, ...errorDefault }
}

export const errorHandler = (errorStore.errors, store) => {
  server = errorStore.errors;
  store_ = store;
  return handleServerValidationErrors;
}

export const clearerrorStore.errors = () => {
  store_.error = null;
  server.value = { ...errorDefault }
}

const handleServerValidationErrors = (error, srv) => {
  clearerrorStore.errors()
  if (error instanceof AxiosError) {
    let rsp;
    if (!(rsp = error.response.data)) {
      server.value.general = 'An unexpected error occurred'
      return
    }
    if (Array.isArray(rsp)) {
      rsp.forEach((item) => {
        const { field: errorField, message: errorMessage } = item
        if (errorField) {
          server.value[errorField] = errorMessage
        } else {
          server.value.general = errorMessage
        }
      })
    } else {
      const { field: errorField, message: errorMessage } = rsp
      if (['database', 'argument'].includes(errorField)) {
        server.value.general = errorMessage
        srv.value = errorMessage
      } else {
        if (errorField) {
          server.value[errorField] = errorMessage
        } else {
          server.value.general = errorMessage
        }
      }
    }
  } else {
    notify({
      title: 'An unexpected error occurred',
      message: error,
      type: 'error',
    })
  }
}
