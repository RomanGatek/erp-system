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
    ...$default
  })
}

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

export const objectDeepCopy = object => {
  JSON.parse(JSON.stringify(object))
}
