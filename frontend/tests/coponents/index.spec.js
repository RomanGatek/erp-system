import * as Components from '@/components'; // Adjust the path if needed
import * as IndeXomponents from '@/components'
import { describe, it, expect } from 'vitest';

describe('Components common Barrel File', () => {
  const expectedExports = [
    'BaseCheckbox',
    'BaseInput',
    'DataTable',
    'EmptyState',
    'Modal',
    'NotificationWrapper',
    'Pagination',
    'PasswordInput',
    'SearchBar',
    'StatusBar',
    'SearchSelect',
  ];

  expectedExports.forEach((componentName) => {
    it(`should export ${componentName}`, () => {
      expect(Components[componentName]).toBeDefined();
    });
  });
});


import * as Components2 from '@/components'; // Adjust the path as necessary

describe('Components ui Barrel File', () => {
  const expectedExports = [
    'Button',
    'NavBarLink',
    'NavBarLinkDropdown'
  ];

  expectedExports.forEach((componentName) => {
    it(`should export ${componentName}`, () => {
      expect(Components2[componentName]).toBeDefined();
    });
  });
});
