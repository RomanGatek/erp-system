import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import App from '@/App.vue';

// Create a persistent mock for the me store
const meStoreMock = {
  user: null,
};

vi.mock('@/stores/me.store.js', () => ({
  useMeStore: () => meStoreMock,
}));

describe('App.vue', () => {
  beforeEach(() => {
    // Create and set active pinia for each test
    const pinia = createPinia();
    setActivePinia(pinia);
    
    // Reset the user to unauthenticated state before each test
    meStoreMock.user = null;
  });

  it('should always render NotificationWrapper and CustomFooter', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [createPinia()],
        stubs: ['router-view', 'CustomNavbar', 'CustomFooter', 'NotificationWrapper']
      }
    });

    // NotificationWrapper and CustomFooter should be rendered regardless of authentication
    const notificationWrapper = wrapper.findComponent({ name: 'NotificationWrapper' });
    const customFooter = wrapper.findComponent({ name: 'CustomFooter' });

    expect(notificationWrapper.exists()).toBe(true);
    expect(customFooter.exists()).toBe(true);
  });

  it('should not render CustomNavbar when user is not authenticated', () => {
    meStoreMock.user = null; // Unauthenticated state

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia()],
        stubs: ['router-view', 'CustomNavbar', 'CustomFooter', 'NotificationWrapper']
      }
    });

    const customNavbar = wrapper.findComponent({ name: 'CustomNavbar' });
    expect(customNavbar.exists()).toBe(false);
  });

  it('should render CustomNavbar when user is authenticated', () => {
    meStoreMock.user = { id: 1, name: 'Test User' }; // Authenticated state

    const wrapper = mount(App, {
      global: {
        plugins: [createPinia()],
        stubs: ['router-view', 'CustomNavbar', 'CustomFooter', 'NotificationWrapper']
      }
    });

    const customNavbar = wrapper.findComponent({ name: 'CustomNavbar' });
    expect(customNavbar.exists()).toBe(true);
  });
});
