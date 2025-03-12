<template>
  <div class="product-card">
    <div class="product-image">
      <img :src="product.image" :alt="product.name" />
      <span
        class="category-tag"
        :class="{
          'bg-slate-100 text-slate-600': product.category.color === 'slate',
          'bg-gray-100 text-gray-600': product.category.color === 'gray',
          'bg-zinc-100 text-zinc-600': product.category.color === 'zinc',
          'bg-neutral-100 text-neutral-600': product.category.color === 'neutral',
          'bg-stone-100 text-stone-600': product.category.color === 'stone',
          'bg-red-100 text-red-600': product.category.color === 'red',
          'bg-orange-100 text-orange-600': product.category.color === 'orange',
          'bg-amber-100 text-amber-600': product.category.color === 'amber',
          'bg-yellow-100 text-yellow-600': product.category.color === 'yellow',
          'bg-lime-100 text-lime-600': product.category.color === 'lime',
          'bg-green-100 text-green-600': product.category.color === 'green',
          'bg-emerald-100 text-emerald-600': product.category.color === 'emerald',
          'bg-teal-100 text-teal-600': product.category.color === 'teal',
          'bg-cyan-100 text-cyan-600': product.category.color === 'cyan',
          'bg-sky-100 text-sky-600': product.category.color === 'sky',
          'bg-blue-100 text-blue-600': product.category.color === 'blue',
          'bg-indigo-100 text-indigo-600': product.category.color === 'indigo',
          'bg-violet-100 text-violet-600': product.category.color === 'violet',
          'bg-purple-100 text-purple-600': product.category.color === 'purple',
          'bg-fuchsia-100 text-fuchsia-600': product.category.color === 'fuchsia',
          'bg-pink-100 text-pink-600': product.category.color === 'pink',
          'bg-rose-100 text-rose-600': product.category.color === 'rose',
        }"
      >
        {{ product.category.name }}
      </span>
    </div>
    <div class="product-details">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-footer">
        <span class="product-price">{{ formatPrice(product.price) }} Kč,-</span>
        <button
          @click="$emit('add-to-cart', product)"
          class="add-to-cart-btn"
          :disabled="isInCart"
        >
          {{ isInCart ? 'In Cart' : 'Add to Cart' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    },
    isInCart: {
      type: Boolean,
      default: false
    }
  },
  emits: ['add-to-cart'],
  methods: {
    formatPrice(price) {
      return new Intl.NumberFormat('cs-CZ').format(price);
    }
  }
}
</script>

<style scoped>
.product-card {
  background-color: white;
  border-radius: 0.75rem;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.product-image {
  position: relative;
  height: 100px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.category-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 0.65rem;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-weight: 500;
}

.product-details {
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
  line-height: 1.2;
}

.product-description {
  font-size: 0.75rem;
  color: #6b7280;
  margin-bottom: 0.75rem;
  line-height: 1.2;
  flex-grow: 1;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.product-price {
  font-weight: 600;
  font-size: 0.9rem;
}

.add-to-cart-btn {
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.add-to-cart-btn:hover {
  background-color: #4338ca;
}

.add-to-cart-btn:disabled {
  background-color: #c7d2fe;
  cursor: not-allowed;
}
</style> 