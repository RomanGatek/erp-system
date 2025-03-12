import VueNativeSock from 'vue-native-websocket-vue3'
import { useSocketStore } from '@/stores/socket.store'

export const setupWebSocket = (app) => {
  console.log('🛜 Setting up WebSocket...')
  
  try {
    // Získáme instanci store
    const socketStore = useSocketStore()
    
    // Nastavení WebSocketu s Pinia store
    app.use(VueNativeSock, 'ws://localhost:8080/ws', {
      store: socketStore,
      format: 'json',
      reconnection: true,
      reconnectionAttempts: 5,
      reconnectionDelay: 3000,
      connectManually: false, // Nastavíme na false, aby se připojení navázalo automaticky
      passToStoreHandler: (eventName, event) => {
        const method = eventName.toUpperCase()

        if (socketStore && typeof socketStore[method] === 'function') {
          if (event) {
            socketStore[method](event)
          } else {
            socketStore[method]()
          }
          return true
        }
        return false
      }
    })
    
    // Uložíme referenci na WebSocket instanci do window pro debugging
    setTimeout(() => {
      // Pokus o získání WebSocket instance
      try {
        const ws = app.config.globalProperties.$socket
        if (ws) {
          console.log('🛜 WebSocket instance found in app.config.globalProperties.$socket')
          // Uložíme instanci do store
          socketStore.socketInstance = ws
        } else {
          console.warn('🛜 WebSocket instance not found in app.config.globalProperties.$socket')
        }
      } catch (error) {
        console.error('🛜 Error accessing WebSocket instance:', error)
      }
    }, 1000)
    
    console.log('🛜 WebSocket setup completed')
  } catch (error) {
    console.error('🛜 Error setting up WebSocket:', error)
  }
}

// Exportujeme funkci sendMessage ze store pro snadnější použití
export const sendMessage = (message) => {
  const store = useSocketStore()
  return store.sendMessage(message)
}