<template>
  <div>
    <!-- Credit Card Preview -->
    <div class="perspective-1000 relative h-48 w-80 mx-auto mb-6">
      <div 
        class="relative w-full h-full transition-all duration-500 transform-gpu preserve-3d"
        :class="{ 'rotate-y-180': isFlipped }"
      >
        <!-- Front of the card -->
        <div 
          class="absolute inset-0 w-full h-full rounded-xl p-4 text-white shadow-xl backface-hidden"
          :class="[
            cardType === 'visa' ? 'bg-gradient-to-br from-black to-blue-900' : 
            cardType === 'mastercard' ? 'bg-gradient-to-br from-red-600 to-yellow-500' : 
            'bg-gradient-to-br from-blue-600 to-blue-400'
          ]"
        >
          <div class="h-full flex flex-col justify-between">
            <!-- Chip and Network -->
            <div class="flex justify-between items-start">
              <div class="w-10 h-8 bg-yellow-300/80 rounded-lg relative overflow-hidden">
                <div class="absolute inset-0 bg-gradient-to-br from-yellow-200 to-yellow-400"></div>
                <div class="absolute inset-0 grid grid-cols-3 gap-0.5 p-1">
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                  <div class="bg-yellow-300/50 rounded-sm"></div>
                </div>
              </div>
              <!-- Card type logo -->
              <div class="w-12 h-10 flex items-center justify-center">
                <svg v-if="cardType === 'visa'" class="w-12 h-8 text-white" viewBox="0 0 1000 324" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M651.19 0.71L405.55 323.29H302.69L180.97 67.47C173.108 51.4531 166.544 42.9666 154.658 35.326C142.773 27.6854 129.316 23.0437 100.5 19.49L2.69 19.63L0.5 32.27C38.5 40.27 82.06 53.55 119.5 67.47L229.04 323.29H337.5L651.19 0.71Z" fill="currentColor"/>
                  <path d="M675.19 323.29L777.5 0.71H880.44L983.19 323.29H879.68L865.62 277.24H764.28L750.22 323.29H675.19ZM782.9 204.27H848.83L815.86 98.38L782.9 204.27Z" fill="currentColor"/>
                  <path d="M468.01 0.71L383.54 220.71L375.63 186.27C357.457 134.473 311.068 92.9466 254.12 73.66L330.8 323.14L439.26 323L580.96 0.71H468.01Z" fill="currentColor"/>
                </svg>
                <svg v-else-if="cardType === 'mastercard'" class="w-12 h-8 text-white" viewBox="0 0 1000 618" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="356.26" cy="309.11" r="309" fill="#EB001B" fill-opacity="0.7"/>
                  <circle cx="644.26" cy="309.11" r="309" fill="#F79E1B" fill-opacity="0.7"/>
                  <path d="M500.5 171.456C471.55 195.638 450 230.051 441 271.456C432 312.86 436.5 358.956 455.5 398.456C474.5 437.956 505.67 467.956 544 483.956C582.33 499.956 622.5 501.456 661.5 488.456C700.5 475.456 735 449.456 760 413.956C785 378.456 798.5 335.456 798.5 291.956C798.5 248.456 785.83 205.456 760.5 169.956C735.17 134.456 701 107.956 662 94.4558C623 80.9558 582.67 81.9558 544 97.4558C505.33 112.956 473.5 142.456 454 181.456" fill="#FF5F00" fill-opacity="0.7"/>
                </svg>
                <svg v-else class="w-10 h-10 text-white/90" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/>
                </svg>
              </div>
            </div>

            <!-- Card Number -->
            <div class="text-lg tracking-wider font-medium">
              {{ formattedCardNumber || '•••• •••• •••• ••••' }}
            </div>

            <!-- Card Holder and Expiry -->
            <div class="flex justify-between items-center">
              <div>
                <div class="text-[10px] text-white/80 uppercase">Card Holder</div>
                <div class="text-sm font-medium tracking-wide">{{ cardName || 'YOUR NAME' }}</div>
              </div>
              <div>
                <div class="text-[10px] text-white/80 uppercase">Expires</div>
                <div class="text-sm font-medium">{{ localExpiry || 'MM/YY' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Back of the card -->
        <div 
          class="absolute inset-0 w-full h-full rounded-xl rotate-y-180 backface-hidden"
          :class="[
            cardType === 'visa' ? 'bg-gradient-to-br from-gray-900 to-blue-900' : 
            cardType === 'mastercard' ? 'bg-gradient-to-br from-red-900 to-yellow-800' : 
            'bg-gradient-to-br from-gray-700 to-gray-600'
          ]"
        >
          <div class="h-full flex flex-col">
            <!-- Black strip -->
            <div class="w-full h-10 bg-black mt-5"></div>
            
            <!-- CVC strip -->
            <div class="px-5 mt-5">
              <div class="bg-white/90 h-8 rounded flex items-center justify-end pr-4">
                <span class="font-mono">{{ cardCvc || '•••' }}</span>
              </div>
              <div class="text-[10px] text-white/60 mt-1 text-right">
                Security Code
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Input Form -->
    <div class="max-w-xs mx-auto space-y-3">
      <div>
        <label class="block text-xs font-medium text-gray-700 mb-1">Card Number</label>
        <div class="grid grid-cols-4 gap-2">
          <input
            ref="cardInput1"
            type="text"
            :value="cardGroup1"
            @input="updateCardGroup($event, 1)"
            @keydown="handleKeyDown($event, 1)"
            class="w-full px-2 py-1.5 text-sm border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-center"
            :class="[cardNumberError ? 'border-red-500' : 'border-gray-300']"
            placeholder="0000"
            maxlength="4"
          >
          <input
            ref="cardInput2"
            type="text"
            :value="cardGroup2"
            @input="updateCardGroup($event, 2)"
            @keydown="handleKeyDown($event, 2)"
            class="w-full px-2 py-1.5 text-sm border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-center"
            :class="[cardNumberError ? 'border-red-500' : 'border-gray-300']"
            placeholder="0000"
            maxlength="4"
          >
          <input
            ref="cardInput3"
            type="text"
            :value="cardGroup3"
            @input="updateCardGroup($event, 3)"
            @keydown="handleKeyDown($event, 3)"
            class="w-full px-2 py-1.5 text-sm border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-center"
            :class="[cardNumberError ? 'border-red-500' : 'border-gray-300']"
            placeholder="0000"
            maxlength="4"
          >
          <input
            ref="cardInput4"
            type="text"
            :value="cardGroup4"
            @input="updateCardGroup($event, 4)"
            @keydown="handleKeyDown($event, 4)"
            class="w-full px-2 py-1.5 text-sm border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-center"
            :class="[cardNumberError ? 'border-red-500' : 'border-gray-300']"
            placeholder="0000"
            maxlength="4"
          >
        </div>
        <p v-if="cardNumberError" class="mt-1 text-xs text-red-500">{{ cardNumberError }}</p>
      </div>

      <div>
        <label class="block text-xs font-medium text-gray-700 mb-1">Card Holder Name</label>
        <div class="w-full px-3 py-1.5 text-sm border border-gray-300 rounded-lg bg-gray-50 cursor-not-allowed">
          {{ cardName || 'Card holder name will be auto-filled' }}
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div>
          <CardExpiryPicker
            v-model="localExpiry"
            label="Expiry Date"
            placeholder="MM/YY"
            @update:model-value="$emit('update:expiry', $event)"
          />
        </div>

        <div>
          <label class="block text-xs font-medium text-gray-700 mb-1">
            CVC
            <button 
              @mouseenter="isFlipped = true"
              @mouseleave="isFlipped = false"
              class="ml-1 text-gray-400 hover:text-gray-500"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
              </svg>
            </button>
          </label>
          <input
            type="text"
            v-model="localCvc"
            @focus="isFlipped = true"
            @blur="isFlipped = false"
            class="w-full px-3 py-1.5 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="123"
            maxlength="3"
            @input="onCvcInput"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import CardExpiryPicker from './CardExpiryPicker.vue'

export default {
  name: 'CreditCard',
  components: {
    CardExpiryPicker
  },
  props: {
    modelValue: {
      type: Object,
      default: () => ({
        number: '',
        name: '',
        expiry: '',
        cvc: ''
      })
    },
    name: {
      type: String,
      default: ''
    },
    number: {
      type: String,
      default: ''
    },
    expiry: {
      type: String,
      default: ''
    },
    cvc: {
      type: String,
      default: ''
    }
  },
  emits: ['update:number', 'update:name', 'update:expiry', 'update:cvc', 'update:paymentMethod'],
  setup(props, { emit }) {
    const isFlipped = ref(false)
    const cardInput1 = ref(null)
    const cardInput2 = ref(null)
    const cardInput3 = ref(null)
    const cardInput4 = ref(null)
    const localExpiry = ref(props.expiry || props.modelValue.expiry || '')
    const localCvc = ref(props.cvc || props.modelValue.cvc || '')
    const cardNumberError = ref('')
    const cardType = ref('')
    
    // Separate card number into 4 groups
    const cardGroup1 = ref('')
    const cardGroup2 = ref('')
    const cardGroup3 = ref('')
    const cardGroup4 = ref('')
    
    // Initialize card groups from modelValue
    watch(() => props.number || props.modelValue.number, (newValue) => {
      if (newValue) {
        const groups = newValue.replace(/\s/g, '').match(/.{1,4}/g) || ['', '', '', '']
        cardGroup1.value = groups[0] || ''
        cardGroup2.value = groups[1] || ''
        cardGroup3.value = groups[2] || ''
        cardGroup4.value = groups[3] || ''
        
        // Detect card type when number changes
        detectCardType(newValue.replace(/\s/g, ''))
      }
    }, { immediate: true })
    
    // Update local expiry when props change
    watch(() => props.expiry || props.modelValue.expiry, (newValue) => {
      localExpiry.value = newValue
    })

    // Update local CVC when props change
    watch(() => props.cvc || props.modelValue.cvc, (newValue) => {
      localCvc.value = newValue
    })
    
    // Log when name changes to help debug
    watch(() => props.name, (newValue) => {
      console.log('Name prop changed:', newValue)
      emit('update:name', newValue) // Propagate change back to parent
    }, { immediate: true })

    const formattedCardNumber = computed(() => {
      const groups = [cardGroup1.value, cardGroup2.value, cardGroup3.value, cardGroup4.value]
      return groups.filter(g => g).join(' ')
    })

    const cardName = computed(() => {
      return props.name || props.modelValue.name || ''
    })

    const cardCvc = computed(() => {
      return localCvc.value
    })
    
    // Detect card type based on first digits
    const detectCardType = (cardNumber) => {
      if (!cardNumber) {
        cardType.value = ''
        return
      }
      
      // Testovací karta začínající s 4 bude Visa
      if (cardNumber === '4444444444444444' || /^4/.test(cardNumber)) {
        cardType.value = 'visa'
        return
      }
      
      // Testovací karta 8888... bude Mastercard
      if (cardNumber === '8888888888888888') {
        cardType.value = 'mastercard'
        return
      }
      
      // Mastercard starts with 51-55 or range 2221-2720
      if (/^5[1-5]/.test(cardNumber) || 
          (/^2/.test(cardNumber) && (parseInt(cardNumber.substr(0, 4)) >= 2221 && parseInt(cardNumber.substr(0, 4)) <= 2720))) {
        cardType.value = 'mastercard'
        return
      }
      
      // Unknown card type
      cardType.value = ''
    }
    
    // Card number validation using Luhn algorithm
    const isValidCardNumber = (number) => {
      if (!number) return false
      
      // Remove spaces and check if contains only digits
      const digits = number.replace(/\s+/g, '')
      if (!/^\d+$/.test(digits)) return false
      
      // Testovací karty - vždy platné
      if (digits === '8888888888888888' || digits === '4444444444444444') {
        return true
      }
      
      // Check if length is valid (most cards are 13-19 digits)
      if (digits.length < 13 || digits.length > 19) return false
      
      // Luhn algorithm (mod 10)
      let sum = 0
      let shouldDouble = false
      
      // Loop from right to left
      for (let i = digits.length - 1; i >= 0; i--) {
        let digit = parseInt(digits.charAt(i))
        
        if (shouldDouble) {
          digit *= 2
          if (digit > 9) digit -= 9
        }
        
        sum += digit
        shouldDouble = !shouldDouble
      }
      
      return sum % 10 === 0
    }
    
    // Handle input for card number groups
    const updateCardGroup = (event, groupNum) => {
      const value = event.target.value.replace(/\D/g, '')
      
      // Update the corresponding group
      switch(groupNum) {
        case 1: cardGroup1.value = value; break
        case 2: cardGroup2.value = value; break
        case 3: cardGroup3.value = value; break
        case 4: cardGroup4.value = value; break
      }
      
      // Auto-focus next input if this one is full
      if (value.length === 4) {
        if (groupNum < 4) {
          const nextInput = {
            1: cardInput2,
            2: cardInput3,
            3: cardInput4
          }[groupNum]
          
          nextInput.value?.focus()
        }
      }
      
      // Combine all groups and emit the full card number
      const fullNumber = [cardGroup1.value, cardGroup2.value, cardGroup3.value, cardGroup4.value].join(' ').trim()
      emit('update:number', fullNumber)
      
      // Detect card type
      detectCardType(fullNumber.replace(/\s/g, ''))
      
      // Validate card number (but only if all 4 groups have content)
      const hasContent = cardGroup1.value && cardGroup2.value && cardGroup3.value && cardGroup4.value
      if (hasContent) {
        if (!isValidCardNumber(fullNumber.replace(/\s/g, ''))) {
          cardNumberError.value = 'Neplatné číslo karty'
        } else {
          cardNumberError.value = ''
        }
      } else {
        cardNumberError.value = ''
      }
    }
    
    // Handle key navigation between inputs
    const handleKeyDown = (event, groupNum) => {
      // If backspace is pressed on an empty input, focus previous input
      if (event.key === 'Backspace' && event.target.value === '') {
        if (groupNum > 1) {
          const prevInput = {
            2: cardInput1,
            3: cardInput2,
            4: cardInput3
          }[groupNum]
          
          prevInput.value?.focus()
          // Put cursor at the end of the input
          setTimeout(() => {
            const length = prevInput.value.value.length
            prevInput.value.setSelectionRange(length, length)
          }, 0)
        }
      }
    }

    // Handle CVC input
    const onCvcInput = () => {
      // Ensure only digits
      localCvc.value = localCvc.value.replace(/\D/g, '')
      emit('update:cvc', localCvc.value)
    }

    return {
      isFlipped,
      cardInput1,
      cardInput2,
      cardInput3,
      cardInput4,
      cardGroup1,
      cardGroup2,
      cardGroup3,
      cardGroup4,
      localExpiry,
      localCvc,
      formattedCardNumber,
      cardName,
      cardCvc,
      cardType,
      updateCardGroup,
      handleKeyDown,
      onCvcInput,
      cardNumberError
    }
  }
}
</script>

<style scoped>
.perspective-1000 {
  perspective: 1000px;
}

.preserve-3d {
  transform-style: preserve-3d;
}

.backface-hidden {
  backface-visibility: hidden;
}

.rotate-y-180 {
  transform: rotateY(180deg);
}
</style> 