// Volání vi.mock MUSÍ být nahoře, před jakýmikoli importy
vi.mock('vue/dist/vue.esm-bundler', () => {
  return {
    createApp: vi.fn(() => ({
      use: vi.fn().mockReturnThis(),
      mount: vi.fn()
    }))
  };
});

vi.mock('pinia', () => {
  return {
    createPinia: vi.fn(() => ({ /* dummy instance */ }))
  };
});

vi.mock('@kyvg/vue3-notification', () => {
  return {
    default: {} // nebo libovolný objekt, dle potřeby
  };
});

vi.mock('@/router', () => {
  return {
    default: {} // dummy hodnota
  };
});

vi.mock('@/App.vue', () => {
  return {
    default: {} // dummy hodnota
  };
});

vi.mock('./assets/tailwind.css', () => ({}));

// Následují importy, které využívají zmačkované moduly
import { createApp } from 'vue/dist/vue.esm-bundler';
import { createPinia } from 'pinia';
import Notifications from '@kyvg/vue3-notification';
import router from '@/router';
import App from '@/App.vue';

import { describe, it, expect, vi, beforeEach } from 'vitest';

describe('Main application entry', () => {
  let appInstance;
  let piniaInstance;

  beforeEach(async () => {
    vi.clearAllMocks();
    vi.resetModules();

    // Dynamicky importujeme hlavní modul, který inicializuje aplikaci
    await import('@/main');

    // Uložíme si instanci Vue aplikace z mocku createApp
    appInstance = createApp.mock.results[0].value;
    // Uložíme si také instanci Pinia, aby bylo možné ověřit pořadí volání
    piniaInstance = createPinia.mock.results[0].value;
  });

  it('should create Vue app with App component', () => {
    expect(createApp).toHaveBeenCalledWith(App);
  });

  it('should use Pinia store', () => {
    expect(createPinia).toHaveBeenCalled();
    expect(appInstance.use).toHaveBeenCalledWith(piniaInstance);
  });

  it('should use Vue Router', () => {
    expect(appInstance.use).toHaveBeenCalledWith(router);
  });

  it('should use Notifications plugin', () => {
    expect(appInstance.use).toHaveBeenCalledWith(Notifications);
  });

  it('should mount app to #app', () => {
    expect(appInstance.mount).toHaveBeenCalledWith('#app');
  });

  it('should initialize plugins in correct order', () => {
    const calls = appInstance.use.mock.calls;
    // První volání: Pinia
    expect(calls[0][0]).toBe(piniaInstance);
    // Druhé volání: router
    expect(calls[1][0]).toBe(router);
    // Třetí volání: Notifications plugin
    expect(calls[2][0]).toBe(Notifications);
    // Mount musí být voláno pouze jednou po inicializaci všech pluginů
    expect(appInstance.mount).toHaveBeenCalledTimes(1);
  });
});
