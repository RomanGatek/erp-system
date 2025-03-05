import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

import chalk from 'chalk'

/**
 * @param {Array} array
 * @param file
 */
function fileContains(file, array) {
  let contains = false;
  array.forEach(item => {
    if (file.includes(item)) contains = true
  });
  return contains;
}

function reloadingStoreUtils() {
  return {
    name: 'reloading-stores-utils',
    enforce: 'post',
    handleHotUpdate({ file, server }) {
      if (fileContains(file, ['store', 'utils'])) {
        const reloadMessage = chalk.bold("[vite] ") + chalk.bgBlue.black(' i ') + " reloading store/utils files..."
        const date = new Date()
        const hours   = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')

        // 3. Poskládáme finální řetězec "HH:MM:SS"
        const timeString = `${hours}:${minutes}:${seconds}`

        console.log(timeString + ' ' + reloadMessage)
        server.ws.send({ type: 'full-reload', path: '*' })
      }
    },
  }
}


export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    tailwindcss(),
    reloadingStoreUtils()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
