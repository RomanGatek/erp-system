<template>
  <button type="button" :class="[
    'border-none rounded-full py-1.5 px-3 text-[0.75rem] cursor-pointer transition-all disabled:cursor-not-allowed disabled:shadow-none',
    typeClasses,
  ]" v-bind="$attrs">
    <slot />
  </button>
</template>

<script>
export default {
  name: 'BaseButton',
  props: {
    type: {
      type: String,
      default: 'primary',
      validator: (value) =>
        ['primary', 'secondary', 'success', 'warning', 'error', 'info', 'pass'].includes(value),
    },
  },
  computed: {
    typeClasses() {
      const classes = {
        primary:
          'bg-gradient-to-r from-blue-500 to-blue-500/70 text-white hover:bg-indigo-700 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
        secondary:
          'bg-gradient-to-r from-gray-100 to-gray-300/70 text-gray-700 hover:bg-gray-500 hover:shadow-md disabled:bg-gray-50 disabled:text-gray-400',
        success:
          'bg-gradient-to-r from-emerald-400 to-emerald-400/70 text-white hover:bg-emerald-700 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
        warning:
          'bg-gradient-to-r from-amber-500 to-amber-500/70 text-white hover:bg-amber-600 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
        error:
          'bg-gradient-to-r from-red-500 to-red-500/70 text-white hover:bg-red-700 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
        info: 'bg-gradient-to-r from-sky-500 to-sky-500/70 text-white hover:bg-sky-600 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
        pass: 'bg-gradient-to-r from-teal-600 to-teal-600/70 text-white hover:bg-teal-700 hover:shadow-md disabled:bg-gray-300 disabled:text-gray-500',
      }
      return classes[this.type]
    },
  },
}
</script>
