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

