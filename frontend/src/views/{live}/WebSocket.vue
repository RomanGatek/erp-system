<!-- filepath: src/components/ProductNotifications.vue -->
<template>
  <div class="test-page">
    <div class="websocket-status" :class="{ connected: isConnected, disconnected: !isConnected }">
      <div class="status-indicator">
        <span
          class="status-dot"
          :class="{ connected: isConnected, disconnected: !isConnected }"
        ></span>
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
        {{ notifications.length }}
        {{ notifications.length === 1 ? 'notification' : 'notifications' }}
        <button
          @click="clearNotifications"
          class="px-2 py-1 bg-gray-200 text-gray-700 rounded text-xs ml-2"
        >
          Clear
        </button>
      </div>
      <div
        v-for="(notification, index) in notifications"
        :key="index"
        class="notification"
        :class="{
          info: notification.type === 'info',
          success: notification.type === 'success',
          error: notification.type === 'error',
          warning: notification.type === 'warning' || notification.type === 'update',
        }"
      >
        <div class="notification-header">
          <span class="notification-type" v-if="notification.type">{{ notification.type }}</span>
          <span class="notification-time">{{ formatTime(notification.timestamp) }}</span>
        </div>
        <div class="notification-content">
          <div v-if="isJsonMessage(notification)">
            <json-tree :data="parseJsonMessage(notification)" :level="1"></json-tree>
          </div>
          <div v-else>{{ formatNotification(notification) }}</div>
        </div>
        <div v-if="notification.serverSessionId" class="notification-session">
          Session ID: {{ notification.serverSessionId }}
        </div>
      </div>
    </div>

    <div class="debug-section" v-if="showDebug">
      <h3>Debug Information</h3>
      <div class="debug-info">
        <p><strong>WebSocket State:</strong> {{ getWebSocketState() }}</p>
        <p><strong>Last Message:</strong> {{ socketStore.message }}</p>
      </div>
    </div>

    <div class="mt-4">
      <button
        @click="showDebug = !showDebug"
        class="px-2 py-1 bg-gray-200 text-gray-700 rounded text-xs"
      >
        {{ showDebug ? 'Hide' : 'Show' }} Debug Info
      </button>
    </div>
  </div>
</template>

<script setup>
import { useSocketStore } from '@/stores/socket.store'
import { storeToRefs } from 'pinia'
import { onMounted, onUnmounted, ref, defineComponent, computed } from 'vue'

// Komponenta pro zobrazení JSON stromu
const JsonTree = defineComponent({
  name: 'JsonTree',
  props: {
    data: {
      type: [Object, Array, String, Number, Boolean, null],
      required: true,
    },
    level: {
      type: Number,
      default: 0,
    },
  },
  setup(props) {
    const isExpanded = ref(props.level <= 1) // První úroveň je automaticky rozbalená

    const toggle = () => {
      isExpanded.value = !isExpanded.value
    }

    // Metody pro animaci rozbalování/sbalování
    const expandEnter = (element) => {
      const height = element.scrollHeight
      element.style.height = '0px'
      element.style.opacity = '0'
      // Vynutíme reflow
      element.offsetHeight
      element.style.height = height + 'px'
      element.style.opacity = '1'
    }

    const expandAfterEnter = (element) => {
      element.style.height = 'auto'
    }

    const expandLeave = (element) => {
      const height = element.scrollHeight
      element.style.height = height + 'px'
      // Vynutíme reflow
      element.offsetHeight
      element.style.height = '0px'
      element.style.opacity = '0'
    }

    // Kontrola, zda je hodnota primitivní (ne objekt nebo pole)
    const isPrimitive = (value) => {
      return value === null || typeof value !== 'object'
    }

    // Získání typu hodnoty pro CSS třídu
    const getValueType = (value) => {
      if (value === null) return 'null'
      return typeof value
    }

    // Formátování hodnoty pro zobrazení
    const formatValue = (value) => {
      if (value === null) return 'null'
      if (typeof value === 'string') return `"${value}"`
      return String(value)
    }

    const isObject = computed(() => {
      return props.data !== null && typeof props.data === 'object'
    })

    const isArray = computed(() => {
      return Array.isArray(props.data)
    })

    const getType = computed(() => {
      if (props.data === null) return 'null'
      if (Array.isArray(props.data)) return 'array'
      return typeof props.data
    })

    const getObjectLength = computed(() => {
      if (isArray.value) {
        return props.data.length
      }
      if (isObject.value) {
        return Object.keys(props.data).length
      }
      return 0
    })

    const getObjectPreview = computed(() => {
      if (!isObject.value || isArray.value) return 'Object'

      // Pokud je to objekt, zobrazíme první vlastnost jako náhled
      const keys = Object.keys(props.data)
      if (keys.length > 0) {
        const firstKey = keys[0]
        const value = props.data[firstKey]
        if (value !== null && typeof value === 'object') {
          return `{${firstKey}: {...}}`
        } else if (typeof value === 'string') {
          const shortValue = value.length > 10 ? value.substring(0, 10) + '...' : value
          return `{${firstKey}: "${shortValue}"...}`
        } else {
          return `{${firstKey}: ${value}...}`
        }
      }

      return '{}'
    })

    const getPreview = computed(() => {
      if (isArray.value) {
        return `(${getObjectLength.value})`
      }
      if (isObject.value) {
        return `{${getObjectLength.value}}`
      }
      if (typeof props.data === 'string') {
        return `"${props.data.length > 30 ? props.data.substring(0, 30) + '...' : props.data}"`
      }
      return String(props.data)
    })

    return {
      isExpanded,
      toggle,
      expandEnter,
      expandAfterEnter,
      expandLeave,
      isPrimitive,
      getValueType,
      formatValue,
      isObject,
      isArray,
      getType,
      getObjectLength,
      getPreview,
      getObjectPreview,
    }
  },
  template: `
    <div class="json-tree" :style="{ marginLeft: level > 0 ? '20px' : '0' }">
      <div v-if="isObject" class="json-tree-node" :data-type="isArray ? 'array' : 'object'">
        <div @click="toggle" class="json-tree-toggle" :class="{ expanded: isExpanded }">
          <span class="cursor-pointer toggle-icon" :class="{ expanded: isExpanded }">{{ isExpanded ? '▼' : '►' }}</span>
          <span v-if="isArray" class="json-key">Array</span>
          <span v-else class="json-key">{{ getObjectPreview }}</span>
          <span class="json-preview" v-if="!isExpanded">{{ getPreview }}</span>
        </div>
        <transition
          name="expand"
          @enter="expandEnter"
          @after-enter="expandAfterEnter"
          @leave="expandLeave"
        >
          <div v-if="isExpanded" class="json-tree-content">
            <div v-if="isArray" v-for="(item, index) in data" :key="index" class="json-tree-item">
              <div v-if="isPrimitive(item)" class="json-tree-inline">
                <span class="json-tree-key">[{{ index }}]: </span>
                <span class="json-tree-value" :class="getValueType(item)">
                  {{ formatValue(item) }}
                </span>
              </div>
              <template v-else>
                <div class="json-tree-key">[{{ index }}]:</div>
                <json-tree :data="item" :level="level + 1"></json-tree>
              </template>
            </div>
            <div v-else v-for="(value, key) in data" :key="key" class="json-tree-item">
              <div v-if="isPrimitive(value)" class="json-tree-inline">
                <span class="json-tree-key">{{ key }}: </span>
                <span class="json-tree-value" :class="getValueType(value)">
                  {{ formatValue(value) }}
                </span>
              </div>
              <template v-else>
                <div class="json-tree-key">{{ key }}:</div>
                <json-tree :data="value" :level="level + 1"></json-tree>
              </template>
            </div>
          </div>
        </transition>
      </div>
      <div v-else class="json-tree-value" :class="getType">
        {{ typeof data === 'string' ? '"' + data + '"' : String(data) }}
      </div>
    </div>
  `,
})

// Použití Pinia store s reaktivními referencemi
const socketStore = useSocketStore()
const { isConnected, notifications } = storeToRefs(socketStore)
const autoReconnectEnabled = ref(false)
const showDebug = ref(false)

/**
 * Formátování časového údaje
 * @param {Date|string} timestamp - Časový údaj k formátování
 * @returns {string} - Formátovaný čas
 */
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleTimeString()
}

/**
 * Formátování obsahu notifikace pro zobrazení
 * @param {Object} notification - Objekt notifikace
 * @returns {string} - Formátovaný obsah notifikace
 */
const formatNotification = (notification) => {
  // Pokud má zpráva vlastnost message, použijeme ji
  if (notification.message) {
    // Pokud je message JSON string, zkusíme ho rozparsovat a zobrazit lépe
    if (
      typeof notification.message === 'string' &&
      (notification.message.startsWith('{') || notification.message.startsWith('['))
    ) {
      try {
        const jsonData = JSON.parse(notification.message)
        return JSON.stringify(jsonData, null, 2)
      } catch (e) {
        // Pokud nelze parsovat, vrátíme původní message
        return notification.message
      }
    }
    return notification.message
  }

  // Pokud je to string, vrátíme ho přímo
  if (typeof notification === 'string') {
    return notification
  }

  // Pro různé typy zpráv
  if (notification.type === 'hello') {
    return `Hello message: ${notification.message || 'No message content'}`
  }

  if (notification.type === 'test') {
    return `Test message: ${notification.message || 'No message content'}`
  }

  if (notification.type === 'heartbeat' || notification.type === 'hearbeat') {
    return `Heartbeat message: ${notification.message || 'Alive'}`
  }

  // Pokud nemá message, vrátíme celý objekt jako JSON bez timestamp a serverSessionId
  const { timestamp, type, serverSessionId, ...rest } = notification

  // Pokud je objekt prázdný, vrátíme jednoduchý text
  if (Object.keys(rest).length === 0) {
    return type ? `${type} notification` : 'Empty notification'
  }

  return JSON.stringify(rest, null, 2)
}

/**
 * Odeslání testovací zprávy
 */
const sendTestMessage = () => {
  socketStore.sendMessage({
    type: 'test',
    message: 'Test message from client',
    timestamp: new Date().toISOString(),
  })
}

/**
 * Vyčištění seznamu notifikací
 */
const clearNotifications = () => {
  notifications.value = []
}

/**
 * Získání textové reprezentace stavu WebSocketu
 * @returns {string} - Textový popis stavu WebSocketu
 */
const getWebSocketState = () => {
  if (!socketStore.socketInstance) return 'No WebSocket instance'

  const states = {
    0: 'CONNECTING',
    1: 'OPEN',
    2: 'CLOSING',
    3: 'CLOSED',
  }

  return states[socketStore.socketInstance.readyState] || 'UNKNOWN'
}

/**
 * Manuální znovupřipojení WebSocketu
 * @returns {boolean} - True pokud byl pokus o připojení úspěšný, jinak false
 */
const reconnect = () => {
  try {
    // Nejprve zkontrolujeme, zda existující instance není aktivní
    if (socketStore.socketInstance && socketStore.socketInstance.readyState === 1) {
      return true
    }

    // Vytvoříme nové WebSocket připojení
    const newWs = new WebSocket('ws://localhost:8080/ws')

    // Nastavíme event handlery
    newWs.onopen = (event) => {
      socketStore.SOCKET_ONOPEN({ currentTarget: newWs })
    }

    newWs.onclose = (event) => {
      socketStore.SOCKET_ONCLOSE(event)
    }

    newWs.onerror = (event) => {
      socketStore.SOCKET_ONERROR(event)
    }

    newWs.onmessage = (event) => {
      // Předáme celý event objekt do handleru
      socketStore.SOCKET_ONMESSAGE(event)
    }

    // Přidáme notifikaci o pokusu o připojení
    notifications.value.unshift({
      type: 'info',
      message: 'Attempting to reconnect WebSocket...',
      timestamp: new Date(),
    })

    return true
  } catch (error) {
    console.error('Error reconnecting WebSocket:', error)

    // Přidáme notifikaci o chybě
    notifications.value.unshift({
      type: 'error',
      message: 'Failed to reconnect: ' + error.message,
      timestamp: new Date(),
    })

    return false
  }
}

/**
 * Kontrola, zda je zpráva ve formátu JSON
 * @param {Object} notification - Objekt notifikace
 * @returns {boolean} - True pokud je zpráva JSON, jinak false
 */
const isJsonMessage = (notification) => {
  if (!notification || !notification.message) return false

  // Pokud je message string a začíná { nebo [, zkusíme ho parsovat
  if (
    typeof notification.message === 'string' &&
    (notification.message.startsWith('{') || notification.message.startsWith('['))
  ) {
    try {
      JSON.parse(notification.message)
      return true
    } catch (e) {
      return false
    }
  }

  return false
}

/**
 * Parsování JSON zprávy
 * @param {Object} notification - Objekt notifikace
 * @returns {Object|null} - Parsovaný JSON objekt nebo null
 */
const parseJsonMessage = (notification) => {
  if (!notification || !notification.message) return null

  try {
    return JSON.parse(notification.message)
  } catch (e) {
    return null
  }
}

// Při mountování komponenty nastavíme kontrolu stavu
onMounted(() => {
  // Jednorázová kontrola stavu připojení po 2 sekundách
  setTimeout(() => {
    // Zkontrolujeme stav připojení
    const connectionStatus = socketStore.checkConnection()
    if (!connectionStatus && autoReconnectEnabled.value) {
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
.test-page {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

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
  border-left: 3px solid #e0e0e0;
  margin-bottom: 8px;
}

.notification.info {
  border-left-color: #2196f3;
  background-color: #e3f2fd;
}

.notification.success {
  border-left-color: #4caf50;
  background-color: #e8f5e9;
}

.notification.error {
  border-left-color: #f44336;
  background-color: #ffebee;
}

.notification.warning {
  border-left-color: #ff9800;
  background-color: #fff3e0;
}

.notification:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.notification-type {
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.7rem;
}

.notification-time {
  font-size: 0.8rem;
  color: #757575;
}

.notification-content {
  margin-top: 4px;
  word-break: break-all;
  white-space: pre-wrap;
  font-family: monospace;
}

.notification-session {
  margin-top: 4px;
  font-size: 0.8rem;
  color: #616161;
  font-style: italic;
}

.debug-section {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f5f5f5;
}

.debug-info {
  margin-top: 10px;
  font-family: monospace;
  white-space: pre-wrap;
}

/* Styly pro JSON Tree */
.json-tree {
  font-family: monospace;
  font-size: 14px;
  line-height: 1.4;
}

.json-tree-node {
  margin: 2px 0;
  position: relative;
}

.json-tree-toggle {
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
  padding: 4px 0;
  border-radius: 2px;
  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

.json-tree-toggle:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.json-tree-toggle.expanded {
  background-color: rgba(0, 0, 0, 0.02);
}

.toggle-icon {
  display: inline-block;
  width: 20px;
  height: 20px;
  text-align: center;
  line-height: 20px;
  font-size: 12px;
  color: #555;
  cursor: pointer;
  margin-right: 8px;
  transition:
    transform 0.3s ease,
    color 0.2s ease;
}

.toggle-icon.expanded {
  color: #1976d2;
  transform: rotate(0deg);
}

.toggle-icon:not(.expanded) {
  transform: rotate(-90deg);
}

.json-tree-content {
  margin-left: 28px;
  overflow: hidden;
  transition:
    height 0.3s ease,
    opacity 0.3s ease;
  position: relative;
}

.json-tree-content::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 0;
  bottom: 0;
  width: 1px;
  background-color: #e0e0e0;
  transition: background-color 0.3s ease;
}

.json-tree-node:hover > .json-tree-content::before {
  background-color: #bbdefb;
}

.json-tree-item {
  display: flex;
  margin: 4px 0;
  align-items: flex-start;
  animation: fadeIn 0.3s ease;
  position: relative;
}

.json-tree-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.json-tree-inline {
  display: flex;
  align-items: center;
  animation: fadeIn 0.3s ease;
  padding: 2px 4px;
  border-radius: 2px;
  transition: background-color 0.2s ease;
}

.json-tree-inline:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-5px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.json-tree-key {
  color: #7e57c2; /* Fialová pro klíče */
  margin-right: 8px;
  white-space: nowrap;
  transition: color 0.2s ease;
  font-weight: 500;
}

.json-tree-toggle:hover .json-tree-key {
  color: #5e35b1;
}

.json-tree-value {
  display: inline-block;
  transition: color 0.2s ease;
  padding: 0 4px;
  border-radius: 2px;
}

.json-tree-value.string {
  color: #e53935; /* Červená pro řetězce */
  background-color: rgba(229, 57, 53, 0.05);
}

.json-tree-value.number {
  color: #1e88e5; /* Modrá pro čísla */
  background-color: rgba(30, 136, 229, 0.05);
}

.json-tree-value.boolean {
  color: #43a047; /* Zelená pro boolean */
  background-color: rgba(67, 160, 71, 0.05);
  font-weight: 600;
}

.json-tree-value.null {
  color: #757575; /* Šedá pro null */
  background-color: rgba(117, 117, 117, 0.05);
  font-style: italic;
}

.json-preview {
  color: #9e9e9e;
  margin-left: 5px;
  transition: color 0.2s ease;
  font-size: 0.9em;
}

.json-tree-toggle:hover .json-preview {
  color: #757575;
}

/* Animace pro rozbalování/sbalování */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  height: 0;
  opacity: 0;
}

/* Speciální styly pro různé typy objektů */
.json-tree-node[data-type='array'] > .json-tree-toggle > .json-key {
  color: #fb8c00; /* Oranžová pro pole */
}

.json-tree-node[data-type='object'] > .json-tree-toggle > .json-key {
  color: #00897b; /* Tyrkysová pro objekty */
}
</style>
