import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useProductsStore, useNotifier, useWorkflowStore } from '.'

/**
 * Pinia store pro správu WebSocket připojení a zpráv
 * 
 * Poskytuje:
 * - Stav připojení WebSocketu
 * - Správu notifikací přijatých přes WebSocket
 * - Metody pro odesílání zpráv
 * - Automatické zpracování WebSocket událostí
 */
export const useSocketStore = defineStore('socket', () => {
  // State
  const isConnected = ref(false)
  const message = ref('')
  const reconnectError = ref(false)
  const heartBeatInterval = ref(50000)
  const heartBeatTimer = ref(0)
  const notifications = ref([])
  const socketInstance = ref(null)
  const connectionAttempts = ref(0)
  const numberId = ref(0)

  const $notifier = useNotifier()
  const $products = useProductsStore()
  const $workflow = useWorkflowStore()

  /**
   * Handler pro událost otevření WebSocket připojení
   * @param {Event} event - WebSocket onopen event
   */
  function SOCKET_ONOPEN(event) {
    isConnected.value = true
    socketInstance.value = event.currentTarget
    connectionAttempts.value = 0

    if (numberId.value === 0) {
      numberId.value = Date.now()
    }
    
    notifications.value.unshift({
      type: 'success',
      message: 'WebSocket connected',
      timestamp: new Date()
    })
    
    try {
      const ws = event.currentTarget

      setTimeout(() => {
        try {
          const helloMsg = JSON.stringify({ 
            type: 'hello', 
            message: 'Client connected',
            clientId: 'web-client-' + numberId.value,
          })

          ws.send(helloMsg)
          
          notifications.value.unshift({
            type: 'info',
            message: 'Hello message sent to server',
            timestamp: new Date()
          })
        } catch (error) {
          console.error('Failed to send JSON hello message:', error)
        }
      }, 1000)
    } catch (error) {
      console.error('Failed to send hello message:', error)
    }
  }

  /**
   * Handler pro událost zavření WebSocket připojení
   * @param {Event} event - WebSocket onclose event
   */
  function SOCKET_ONCLOSE(event) {
    console.log('🛜 WebSocket Disconnected!', event)
    isConnected.value = false
    socketInstance.value = null
    
    notifications.value.unshift({
      type: 'error',
      message: 'WebSocket disconnected',
      timestamp: new Date()
    })
    
    if (heartBeatTimer.value) {
      clearInterval(heartBeatTimer.value)
      heartBeatTimer.value = 0
    }
  }

  /**
   * Handler pro událost chyby WebSocket připojení
   * @param {Event} event - WebSocket onerror event
   */
  function SOCKET_ONERROR(event) {
    console.error('WebSocket Error:', event)
    
    notifications.value.unshift({
      type: 'error',
      message: 'WebSocket error occurred',
      timestamp: new Date()
    })
  }

  /**
   * Parses the incoming WebSocket message data
   * @param {any} rawData - Raw data from WebSocket message
   * @returns {Object} Parsed message data
   */
  function parseMessageData(rawData) {
    try {
      let parsedData;
      
      if (typeof rawData === 'string') {
        try {
          parsedData = JSON.parse(rawData);
        } catch (e) {
          parsedData = { message: rawData };
        }
      } else if (typeof rawData === 'object') {
        parsedData = rawData;
      } else {
        parsedData = { message: String(rawData) };
      }
      
      if (!parsedData.timestamp) {
        parsedData.timestamp = new Date();
      }
      
      return parsedData;
    } catch (error) {
      console.error('Failed to parse WebSocket message:', error);
      return { 
        type: 'warning',
        message: typeof rawData === 'string' ? rawData : 'Received non-text message',
        timestamp: new Date()
      };
    }
  }

  /**
   * Processes the parsed WebSocket message and updates relevant stores
   * @param {Object} parsedData - Parsed message data
   */
  async function processMessage(parsedData) {
    try {
      if (parsedData.type === 'update') {
        try {
          const json = JSON.parse(parsedData.message);
          console.log('🛜 Received update:', json);
          
          // Generate a unique update ID
          const baseUpdateId = `update-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
          
          // Process the update based on entity type
          if (json.entityType) {
            // Při aktualizaci produktu aktualizujeme všechny související části
            if (json.entityType === 'products' || json.entityType === 'product') {
              // Aktualizace produktů
              await $products.updateSelf({ 
                updateId: `${baseUpdateId}-products`, 
                entity: json.entityType 
              });
              
              // Aktualizace objednávek, protože obsahují produkty
              // Přidáme zpoždění, aby se nejprve aktualizovaly produkty
              setTimeout(async () => {
                await $workflow.updateSelf({
                  updateId: `${baseUpdateId}-workflow`,
                  entity: 'orders' // Použijeme 'orders' jako typ entity pro workflow
                });
              }, 500);
              
              $notifier.info(
                `System updated`, 
                `Products and related data have been updated via WebSocket`
              );
            } 
            // Pokud přijde specifická aktualizace pro objednávky
            else if (json.entityType === 'orders' || json.entityType === 'order' || json.entityType === 'workflow') {
              await $workflow.updateSelf({
                updateId: `${baseUpdateId}-workflow`,
                entity: json.entityType
              });
              
              $notifier.info(
                `Orders updated`, 
                `Orders have been updated via WebSocket`
              );
            }
            // Pokud přijde jakákoliv jiná aktualizace, zalogujeme ji
            else {
              console.log(`Received update for entity type: ${json.entityType}`);
              $notifier.info(
                `Update received`,
                `Received update for ${json.entityType}`
              );
            }
          }
        } catch (error) {
          console.error('Error processing update message:', error);
          $notifier.error('Update Error', 'Failed to process update message');
        }
      }

      // Add the message to notifications
      notifications.value.unshift({
        ...parsedData,
        timestamp: parsedData.timestamp ? new Date(parsedData.timestamp) : new Date()
      });
    } catch (error) {
      console.error('Failed to process WebSocket message:', error);
      notifications.value.unshift({
        type: 'error',
        message: 'Failed to process message: ' + error.message,
        timestamp: new Date()
      });
    }
  }

  /**
   * Handler pro příchozí WebSocket zprávy
   * @param {MessageEvent} event - WebSocket onmessage event
   * 
   * Očekávaný formát zprávy ze serveru:
   * {
   *   type: string,
   *   message: string,
   *   timestamp: string,
   *   clientId: string,
   *   serverSessionId: string
   * }
   */
  async function SOCKET_ONMESSAGE(event) {
    let rawData = null;
    
    if (event && event.data !== undefined) {
      rawData = event.data;
    } else {
      rawData = event;
    }
    
    message.value = rawData;
    
    const parsedData = parseMessageData(rawData);
    await processMessage(parsedData);
  }

  /**
   * Handler pro událost znovupřipojení WebSocketu
   * @param {number} count - Počet pokusů o znovupřipojení
   */
  function SOCKET_RECONNECT(count) {
    connectionAttempts.value = count
    
    notifications.value.unshift({
      type: 'info',
      message: `Reconnection attempt ${count}`,
      timestamp: new Date()
    })
  }

  /**
   * Handler pro událost chyby při znovupřipojení WebSocketu
   */
  function SOCKET_RECONNECT_ERROR() {
    reconnectError.value = true
    
    notifications.value.unshift({
      type: 'error',
      message: 'Reconnection failed',
      timestamp: new Date()
    })
  }
  
  /**
   * Kontrola stavu WebSocket připojení
   * @returns {boolean} - True pokud je připojení aktivní, jinak false
   */
  function checkConnection() {
    if (socketInstance.value) {
      try {
        if (socketInstance.value.readyState === 1) {
          isConnected.value = true
          return true
        } else {
          isConnected.value = false
          return false
        }
      } catch (error) {
        console.error('Error checking connection:', error)
        isConnected.value = false
        return false
      }
    } else {
      isConnected.value = false
      return false
    }
  }
  
  /**
   * Odeslání zprávy přes WebSocket
   * @param {object|string} message - Zpráva k odeslání (objekt bude převeden na JSON)
   * @returns {boolean} - True pokud byla zpráva úspěšně odeslána, jinak false
   */
  function sendMessage(message) {
    if (!checkConnection()) {
      console.error('Cannot send message: WebSocket not connected')
      
      notifications.value.unshift({
        type: 'error',
        message: 'Cannot send message: WebSocket not connected',
        clientId: 'web-client-' + numberId.value,
        timestamp: new Date()
      })
      
      return false
    }
    
    try {
      const messageString = typeof message === 'string' ? message : JSON.stringify(message)
      socketInstance.value.send(messageString)
      
      notifications.value.unshift({
        type: 'info',
        message: 'Message sent: ' + messageString.substring(0, 50) + (messageString.length > 50 ? '...' : ''),
        timestamp: new Date()
      })
      
      return true
    } catch (error) {
      console.error('Error sending message:', error)
      
      notifications.value.unshift({
        type: 'error',
        message: 'Failed to send message: ' + error.message,
        timestamp: new Date()
      })
      
      return false
    }
  }

  return {
    // State
    isConnected,
    message,
    reconnectError,
    heartBeatInterval,
    heartBeatTimer,
    notifications,
    socketInstance,
    connectionAttempts,
    
    // Actions
    SOCKET_ONOPEN,
    SOCKET_ONCLOSE,
    SOCKET_ONERROR,
    SOCKET_ONMESSAGE,
    parseMessageData,
    processMessage,
    SOCKET_RECONNECT,
    SOCKET_RECONNECT_ERROR,
    sendMessage,
    checkConnection
  }
})

/**
 * Helper funkce pro použití WebSocket store mimo setup
 * @returns {ReturnType<typeof useSocketStore>} - Instance WebSocket store
 */
export function useSocketStoreWithOut() {
  return useSocketStore()
} 