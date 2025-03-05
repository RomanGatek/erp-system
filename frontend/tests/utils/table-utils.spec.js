import { setupSort, sort, filter } from '@/utils/table-utils.js'; // adjust the path as necessary
import { describe, it, expect } from 'vitest';

describe('Sorting and Filtering Helpers', () => {
  describe('setupSort()', () => {
    it('should return an object with the given field and an ascending direction', () => {
      const result = setupSort('name');
      expect(result).toEqual({ field: 'name', direction: 'asc' });
    });
  });

  describe('sort()', () => {
    it('should sort the array in ascending order when direction is "asc"', () => {
      const state = {
        sorting: { field: 'name', direction: 'asc' }
      };
      const items = [
        { name: 'Charlie' },
        { name: 'Alice' },
        { name: 'Bob' }
      ];
      // Create a copy of items to avoid mutating the original array.
      const sorted = sort(state, [...items]);
      expect(sorted).toEqual([
        { name: 'Alice' },
        { name: 'Bob' },
        { name: 'Charlie' }
      ]);
    });

    it('should sort the array in descending order when direction is "desc"', () => {
      const state = {
        sorting: { field: 'name', direction: 'desc' }
      };
      const items = [
        { name: 'Charlie' },
        { name: 'Alice' },
        { name: 'Bob' }
      ];
      const sorted = sort(state, [...items]);
      expect(sorted).toEqual([
        { name: 'Charlie' },
        { name: 'Bob' },
        { name: 'Alice' }
      ]);
    });

    it('should not sort the array if the sorting field is "actions"', () => {
      const state = {
        sorting: { field: 'actions', direction: 'asc' }
      };
      const items = [
        { actions: 2, name: 'Charlie' },
        { actions: 1, name: 'Alice' },
        { actions: 3, name: 'Bob' }
      ];
      // When the sorting field is "actions", the array remains unsorted.
      const sorted = sort(state, [...items]);
      expect(sorted).toEqual(items);
    });
  });

  describe('filter()', () => {
    it('should filter items based on searchQuery and sort them', () => {
      const state = {
        items: [
          { name: 'Alice', age: 30 },
          { name: 'Bob', age: 25 },
          { name: 'Charlie', age: 35 }
        ],
        searchQuery: 'a', // this query should match "Alice" and "Charlie"
        sorting: { field: 'name', direction: 'asc' }
      };

      // Filter callback checks if the item name includes the searchQuery (case-insensitive)
      const filterCallback = (item) =>
        item.name.toLowerCase().includes(state.searchQuery.toLowerCase());

      const result = filter(state, filterCallback);
      // Expected filtered items: "Alice" and "Charlie", sorted by name ascending.
      expect(result).toEqual([
        { name: 'Alice', age: 30 },
        { name: 'Charlie', age: 35 }
      ]);
    });

    it('should return all items sorted if searchQuery is falsy', () => {
      const state = {
        items: [
          { name: 'Charlie', age: 35 },
          { name: 'Alice', age: 30 },
          { name: 'Bob', age: 25 }
        ],
        searchQuery: '', // falsy value: no filtering should be applied
        sorting: { field: 'name', direction: 'asc' }
      };

      // The filter callback is defined but will not be used since searchQuery is falsy.
      const filterCallback = (item) =>
        item.name.toLowerCase().includes(state.searchQuery.toLowerCase());

      const result = filter(state, filterCallback);
      // Expected: all items sorted by name ascending.
      expect(result).toEqual([
        { name: 'Alice', age: 30 },
        { name: 'Bob', age: 25 },
        { name: 'Charlie', age: 35 }
      ]);
    });
  });
});
