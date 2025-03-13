import { initMeStore, hasPermission, isLoggedIn } from '@/utils'; // adjust the path as needed
import { describe, it, expect } from 'vitest';

describe('Me Store Helpers', () => {
  describe('hasPermission', () => {
    it('should return false if there is no user', () => {
      initMeStore({ user: null });
      expect(hasPermission('ADMIN')).toBe(false);
      expect(hasPermission('MANAGER')).toBe(false);
      expect(hasPermission('USER')).toBe(false);
    });

    it('should return true for ADMIN when user has ROLE_ADMIN', () => {
      const store = {
        user: {
          roles: {
            admin: { name: 'ROLE_ADMIN' },
            manager: { name: 'ROLE_MANAGER' }
          }
        }
      };
      initMeStore(store);
      expect(hasPermission('ADMIN')).toBe(true);
    });

    it('should return false for ADMIN when user does not have ROLE_ADMIN', () => {
      const store = {
        user: {
          roles: {
            user: { name: 'ROLE_USER' }
          }
        }
      };
      initMeStore(store);
      expect(hasPermission('ADMIN')).toBe(false);
    });

    it('should return true for MANAGER when user has ROLE_MANAGER', () => {
      const store = {
        user: {
          roles: {
            manager: { name: 'ROLE_MANAGER' }
          }
        }
      };
      initMeStore(store);
      expect(hasPermission('MANAGER')).toBe(true);
    });

    it('should return true for USER if a user is logged in regardless of roles', () => {
      const store = {
        user: {
          roles: {
            user: { name: 'ROLE_USER' }
          }
        }
      };
      initMeStore(store);
      expect(hasPermission('USER')).toBe(true);
    });

    it('should return true for an unknown role when user is logged in', () => {
      const store = {
        user: {
          roles: {
            guest: { name: 'ROLE_GUEST' }
          }
        }
      };
      initMeStore(store);
      // Default case returns true if the user exists
      expect(hasPermission('SOMETHING')).toBe(true);
    });
  });

  describe('isLoggedIn computed', () => {
    it('should return false when no user is present', () => {
      initMeStore({ });
      // isLoggedIn is a computed ref, so we access its value
      expect(isLoggedIn.value).toBe(false);
    });
  });
});
