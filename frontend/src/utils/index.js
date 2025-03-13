import { reactive } from 'vue'

/**
 * Creates a reactive object with additional helper methods:
 * - `$clear()`: Resets the object to its default values.
 * - `$assign(object)`: Assigns/merges new properties into the reactive object.
 *
 * @param {{[key: string]: any}} $default The default values for the reactive object.
 */
export const $reactive = ($default) => {
  // We define a unique symbol to store the default values within the reactive object.
  const DEFAULT_SYMBOL = Symbol('$reactive.default')

  // Return a reactive object containing:
  // 1. The default values under the DEFAULT_SYMBOL.
  // 2. A `$clear()` method to revert the object back to its original default values.
  // 3. A `$assign(object)` method to merge new properties into the reactive object.
  // 4. The spread of default properties themselves.
  return reactive({
    // Store default values under the symbol key.
    [DEFAULT_SYMBOL]: Object.assign({}, $default),

    /**
     * Resets all properties on this reactive object to their initial default values,
     * as specified in [DEFAULT_SYMBOL].
     */
    $clear() {
      //console.log(chalk.yellow("DEFAULT: ", JSON.stringify(this[DEFAULT_SYMBOL])))
      //console.log(chalk.green("clearing $reactive ", JSON.stringify(this)))
      Object.assign(this, this[DEFAULT_SYMBOL])
      //console.log(chalk.blue("cleared $reactive ", JSON.stringify(this)))
    },

    /**
     * Assigns the properties of the given object to this reactive object.
     *
     * @param {{[key: string]: any}} object - Object whose properties should be merged into this reactive object.
     */
    $assign(object) {
      Object.keys(object).forEach(o => {
        this[o] = object[o];
      });
    },

    // Spread the default properties so they're directly accessible on the reactive object.
    ...$default,
    $cleaned() {
      return Object.fromEntries(
        Object.keys(this)
          .filter(key => {
            const isSymbol = typeof key === 'symbol';
            const isPrivate = key.startsWith('$');
            const isUndefined = this[key] === undefined;
    
            return !isSymbol && !isPrivate && !isUndefined;
          })
          .map(key => [key, this[key]])
      );
    }
  })
}

/**
 * Formats a price to a string with two decimal places.
 * @param {number} price 
 * @returns 
 */
export const formatPrice = (price) => {
  return (Math.floor(price * 100) / 100).toFixed(2)
}

/**
 * Formats a date string into a human-readable format.
 * @param {string} dateString 
 * @returns 
 */
export const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return date.toLocaleDateString('cs-CZ', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
/**
 * Makes a deep copy of an object.
 * @param {Object} object 
 */
export const objectDeepCopy = object => {
  JSON.parse(JSON.stringify(object))
}


/**
 * paginates the given items with the given pagination settings.
 * @param {any[]} items 
 * @param {number} currentPage 
 * @param {number} perPage 
 * @returns {any[]}
 */
export const paginate = (items, currentPage, perPage) => {
  const start = (currentPage - 1) * perPage;
  const end = start + perPage;
  return items.slice(start, end);
};

/**
 * Paginates the given items with the given pagination settings.
 * @param {any[]} items 
 * @param {{currentPage: number, perPage: number}} pagination 
 * @returns { totalItems: number, totalPages: number, startItem: number, endItem: number }
 */
export const getPaginationInfo = (items, pagination) => {
  const { currentPage, perPage } = pagination

  const totalItems = items.length;
  const totalPages = Math.ceil(totalItems / perPage);
  const startItem = (currentPage - 1) * perPage + 1;
  const endItem = Math.min(startItem + perPage - 1, totalItems);

  return { totalItems, totalPages, startItem, endItem };
}

/**
 * paginate with the given state
 * @param {import('vue').Ref} state
 * @returns {any[]}
 */
export const paginateViaState = (state) => {
  const start = (state.pagination.currentPage - 1) * state.pagination.perPage
  const end = start + state.pagination.perPage
  return state.filtered.slice(start, end)
}


/**
 * setup sorting object
 * @param {string} field 
 * @returns 
 */
export const setupSort = (field) => {
  return { field, direction: 'asc' }
}


/**
 * Apply sorting to the filtered items
 * @param {import("vue").Ref} state 
 * @param {any[]} filtered 
 * @returns 
 */
export const sort = (state, filtered) => {
  if (state.sorting.field !== 'actions') {
    filtered.sort((a, b) => {
      const aVal = a[state.sorting.field]
      const bVal = b[state.sorting.field]
      const direction = state.sorting.direction === 'asc' ? 1 : -1
      return aVal > bVal ? direction : -direction
    })
  }
  return filtered;
}

/**
 * filter functions
 * @param {Object} state
 * @param {Function} filterCallback
 */
export const filter = (state, filterCallback) => {
  let filtered = [...state.items]
  if (state.searchQuery) filtered = filtered.filter(filterCallback)
  return sort(state, filtered)
}

import { computed } from "vue";

let meStore;

export const initMeStore = (store) => {
    meStore = store
}

export const hasPermission = (requiredRole) => {
    if (!meStore.user) return false
    const userRoles = Object.values(meStore.user?.roles ?? []).map(role => role.name)

    switch (requiredRole) {
        case 'ADMIN':
            return userRoles.includes('ROLE_ADMIN')
        case 'MANAGER':
            return userRoles.includes('ROLE_MANAGER')
        case 'USER':
            return true // Každý přihlášený uživatel má základní práva
        default:
            return true
    }
}

export const isLoggedIn = computed(() => {
    return !!meStore.user
})


export function choosedColor(item) {

  if (!item) return

  const selectedColor = item.color
  return {
    // 'bg-slate-50 text-slate-600': item.color === 'slate',
    // 'bg-gray-50 text-gray-600': item.color === 'gray',
    // 'bg-zinc-50 text-zinc-600': item.color === 'zinc',
    // 'bg-neutral-50 text-neutral-600': item.color === 'neutral',
    // 'bg-stone-50 text-stone-600': item.color === 'stone',
    // 'bg-red-50 text-red-600': item.color === 'red',
    // 'bg-orange-50 text-orange-600': item.color === 'orange',
    // 'bg-amber-50 text-amber-600': item.color === 'amber',
    // 'bg-yellow-50 text-yellow-600': item.color === 'yellow',
    // 'bg-lime-50 text-lime-600': item.color === 'lime',
    // 'bg-green-50 text-green-600': item.color === 'green',
    // 'bg-emerald-50 text-emerald-600': item.color === 'emerald',
    // 'bg-teal-50 text-teal-600': item.color === 'teal',
    // 'bg-cyan-50 text-cyan-600': item.color === 'cyan',
    // 'bg-sky-50 text-sky-600': item.color === 'sky',
    // 'bg-blue-50 text-blue-600': item.color === 'blue',
    // 'bg-indigo-50 text-indigo-600': item.color === 'indigo',
    // 'bg-violet-50 text-violet-600': item.color === 'violet',
    // 'bg-purple-50 text-purple-600': item.color === 'purple',
    // 'bg-fuchsia-50 text-fuchsia-600': item.color === 'fuchsia',
    // 'bg-pink-50 text-pink-600': item.color === 'pink',
    // 'bg-rose-50 text-rose-600': item.color === 'rose',

    // // Doplněné třídy:
    // 'border-slate-500 ring-slate-500': item.color === 'slate',
    // 'border-gray-500 ring-gray-500': item.color === 'gray',
    // 'border-zinc-500 ring-zinc-500': item.color === 'zinc',
    // 'border-neutral-500 ring-neutral-500': item.color === 'neutral',
    // 'border-stone-500 ring-stone-500': item.color === 'stone',
    // 'border-red-500 ring-red-500': item.color === 'red',
    // 'border-orange-500 ring-orange-500': item.color === 'orange',
    // 'border-amber-500 ring-amber-500': item.color === 'amber',
    // 'border-yellow-500 ring-yellow-500': item.color === 'yellow',
    // 'border-lime-500 ring-lime-500': item.color === 'lime',
    // 'border-green-500 ring-green-500': item.color === 'green',
    // 'border-emerald-500 ring-emerald-500': item.color === 'emerald',
    // 'border-teal-500 ring-teal-500': item.color === 'teal',
    // 'border-cyan-500 ring-cyan-500': item.color === 'cyan',
    // 'border-sky-500 ring-sky-500': item.color === 'sky',
    // 'border-blue-500 ring-blue-500': item.color === 'blue',
    // 'border-indigo-500 ring-indigo-500': item.color === 'indigo',
    // 'border-violet-500 ring-violet-500': item.color === 'violet',
    // 'border-purple-500 ring-purple-500': item.color === 'purple',
    // 'border-fuchsia-500 ring-fuchsia-500': item.color === 'fuchsia',
    // 'border-pink-500 ring-pink-500': item.color === 'pink',
    // 'border-rose-500 ring-rose-500': item.color === 'rose',
    'bg-slate-50 text-slate-600': selectedColor === 'slate',
    'bg-gray-50 text-gray-600': selectedColor === 'gray',
    'bg-zinc-50 text-zinc-600': selectedColor === 'zinc',
    'bg-neutral-50 text-neutral-600': selectedColor === 'neutral',
    'bg-stone-50 text-stone-600': selectedColor === 'stone',
    'bg-red-50 text-red-600': selectedColor === 'red',
    'bg-orange-50 text-orange-600': selectedColor === 'orange',
    'bg-amber-50 text-amber-600': selectedColor === 'amber',
    'bg-yellow-50 text-yellow-600': selectedColor === 'yellow',
    'bg-lime-50 text-lime-600': selectedColor === 'lime',
    'bg-green-50 text-green-600': selectedColor === 'green',
    'bg-emerald-50 text-emerald-600': selectedColor === 'emerald',
    'bg-teal-50 text-teal-600': selectedColor === 'teal',
    'bg-cyan-50 text-cyan-600': selectedColor === 'cyan',
    'bg-sky-50 text-sky-600': selectedColor === 'sky',
    'bg-blue-50 text-blue-600': selectedColor === 'blue',
    'bg-indigo-50 text-indigo-600': selectedColor === 'indigo',
    'bg-violet-50 text-violet-600': selectedColor === 'violet',
    'bg-purple-50 text-purple-600': selectedColor === 'purple',
    'bg-fuchsia-50 text-fuchsia-600': selectedColor === 'fuchsia',
    'bg-pink-50 text-pink-600': selectedColor === 'pink',
    'bg-rose-50 text-rose-600': selectedColor === 'rose',
  }
}