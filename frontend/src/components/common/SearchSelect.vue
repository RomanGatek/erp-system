<template>
    <div class="relative">
        <BaseInput v-model="searchQuery" placeholder="Hledat..." :label="label" @focus="showDropdown = true"
            @blur="hideDropdown" v-bind="$attrs" />
        <transition name="fade">
            <div v-if="showDropdown"
                class="absolute z-10 w-full bg-white border border-gray-300 rounded-lg mt-1 shadow-lg max-h-60 overflow-y-auto">
                <div v-for="item in filteredItems" :key="item[returnField]"
                    class="flex items-center p-3 cursor-pointer hover:bg-gray-100 rounded-lg transition duration-200"
                    @click="selectItem(item)"
                    :class="{ 'bg-green-100': selectedOption && selectedOption === item }">
                    <div v-if="item.avatar" class="w-8 h-8 rounded-full mr-3">
                        <img :src="item.avatar" alt="" class="w-full h-full rounded-full" />
                    </div>
                    <div v-else class="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center mr-3">
                        <span class="text-white font-bold">P</span>
                    </div>
                    <span class="text-gray-800">{{ item[by] }}</span>
                </div>
                <div class="border-t border-gray-300"></div>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import BaseInput from './BaseInput.vue';

const props = defineProps({
    items: {
        type: Array,
        required: true,
    },
    returnField: {
        type: String,
        default: 'value',
    },
    modelValue: {
        type: Object,
        default: () => ({}),
    },
    by: {
        type: String,
        default: 'label',
    },
    label: {
        type: String,
        default: 'Item',
    },
    placeholder: {
        type: String,
        default: 'Search item...',
    },
});

const emit = defineEmits(['update:modelValue']);

const searchQuery = ref('');
const showDropdown = ref(false);
const selectedOption = ref(null);

const filteredItems = computed(() => {
    return props.items.filter(item =>
        item[props.by].toLowerCase().includes(searchQuery.value.toLowerCase())
    );
});

const selectItem = (item) => {
    selectedOption.value = item;
    searchQuery.value = item[props.by];
    emit('update:modelValue', item);
    showDropdown.value = false;
};

const hideDropdown = () => {
    setTimeout(() => {
        showDropdown.value = false;
    }, 100);
};

onMounted(() => {
    if (props.modelValue) {
        selectedOption.value = props.modelValue;
        searchQuery.value = props.modelValue[props.by];
    }
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.1s ease-in-out;
    /* Zrychlené animace */
}

.fade-enter,
.fade-leave-to

/* .fade-leave-active in <2.1.8 */
    {
    opacity: 0;
}

.fade-leave {
    opacity: 1;
    /* Udržuje plnou viditelnost při odchodu */
}
</style>
