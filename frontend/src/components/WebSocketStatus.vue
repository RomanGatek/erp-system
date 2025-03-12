<template>
  <div class="websocket-status" :class="{ 'connected': isConnected, 'disconnected': !isConnected }">
    <div class="status-indicator">
      <span class="status-dot" :class="{ 'connected': isConnected, 'disconnected': !isConnected }"></span>
      WebSocket status: {{ isConnected ? 'Connected' : 'Disconnected' }}
    </div>

    <div class="actions">
      <button
        v-if="!isConnected"
        @click="reconnect"
        class="px-2 py-1 bg-blue-500 text-white rounded text-xs"
      >
        Reconnect
      </button>

      <button
        v-if="isConnected"
        @click="sendTestMessage"
        class="px-2 py-1 bg-green-500 text-white rounded text-xs ml-2"
      >
        Test Message
      </button>
    </div>
  </div>

  <div v-if="notifications.length > 0" class="notifications-container">
    <div class="notification-count">
      {{ notifications.length }} {{ notifications.length === 1 ? 'notification' : 'notifications' }}
      <button @click="clearNotifications" class="px-2 py-1 bg-gray-200 text-gray-700 rounded text-xs ml-2">
        Clear
      </button>
    </div>
    <div v-for="(notification, index) in notifications" :key="index" class="notification">
      <div class="notification-time">{{ formatTime(notification.timestamp) }}</div>
      <div class="notification-content">{{ formatNotification(notification) }}</div>
    </div>
  </div>
</template>

<script setup>
import { useSocketStore } from '@/stores/socket.store'
import { storeToRefs } from 'pinia'
import { onMounted, onUnmounted, ref } from 'vue'

// Použití Pinia store s reaktivními referencemi
const socketStore = useSocketStore()
const { isConnected, notifications } = storeToRefs(socketStore)
const autoReconnectEnabled = ref(false)

// Formátování času
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleTimeString()
}

// Formátování notifikace
const formatNotification = (notification) => {
  if (typeof notification === 'string') return notification
  if (notification.message) return notification.message

  // Pokud nemá message, vrátíme celý objekt jako JSON
  const { timestamp, ...rest } = notification
  return JSON.stringify(rest)
}

// Funkce pro odeslání testovací zprávy
const sendTestMessage = () => {
  socketStore.sendMessage({
    type: 'test',
    message: 'Test message from client',
    timestamp: new Date().toISOString()
  })
}

// Funkce pro vyčištění notifikací
const clearNotifications = () => {
  notifications.value = []
}

// Funkce pro manuální připojení
const reconnect = () => {
  // Pokud máme přístup k Vue instanci a $connect metodě
  if (window.$vue && window.$vue.config && window.$vue.config.globalProperties && window.$vue.config.globalProperties.$connect) {
    console.log('🛜 Manually reconnecting WebSocket...')
    window.$vue.config.globalProperties.$connect('ws://localhost:8080/ws')
    return true
  } else {
    notifications.value.unshift({
      type: 'warning',
      message: 'Cannot reconnect: WebSocket API not available',
      timestamp: new Date()
    })
    return false
  }
}

// Při mountování komponenty nastavíme kontrolu stavu
onMounted(() => {
  // Jednorázová kontrola stavu připojení po 2 sekundách
  setTimeout(() => {
    // Zkontrolujeme stav připojení
    const connectionStatus = socketStore.checkConnection()
    console.log('🛜 Initial connection check:', connectionStatus)

    // Pokud nejsme připojeni a je povoleno automatické připojení, zkusíme se připojit
    if (!connectionStatus && autoReconnectEnabled.value) {
      console.log('🛜 Not connected, attempting to reconnect...')
      reconnect()
    }
  }, 2000)
})

// Při unmountování komponenty vyčistíme interval
onUnmounted(() => {
  // Nic k vyčištění
})
</script>

<style scoped>
.websocket-status {
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-indicator {
  display: flex;
  align-items: center;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}

.status-dot.connected {
  background-color: #2e7d32;
  box-shadow: 0 0 5px #2e7d32;
}

.status-dot.disconnected {
  background-color: #c62828;
  box-shadow: 0 0 5px #c62828;
}

.connected {
  background-color: #e6f7e6;
  color: #2e7d32;
}

.disconnected {
  background-color: #ffebee;
  color: #c62828;
}

.notifications-container {
  margin-top: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 16px;
  max-height: 300px;
  overflow-y: auto;
}

.notification-count {
  font-weight: 500;
  margin-bottom: 8px;
  color: #424242;
  display: flex;
  align-items: center;
}

.notification {
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.notification:last-child {
  border-bottom: none;
}

.notification-time {
  font-size: 0.8rem;
  color: #757575;
}

.notification-content {
  margin-top: 4px;
  word-break: break-all;
}
</style>
