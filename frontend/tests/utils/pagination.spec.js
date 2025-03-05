import { paginate, getPaginationInfo, __paginate } from '@/utils/pagination'; // adjust the path as needed
import { describe, it, expect } from 'vitest';

describe('Pagination helpers', () => {
  describe('paginate()', () => {
    const items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    it('should return the first page of items', () => {
      expect(paginate(items, 1, 3)).toEqual([1, 2, 3]);
    });

    it('should return the second page of items', () => {
      expect(paginate(items, 2, 3)).toEqual([4, 5, 6]);
    });

    it('should return the last page with fewer items', () => {
      expect(paginate(items, 4, 3)).toEqual([10]);
    });

    it('should return an empty array if the page is out of range', () => {
      expect(paginate(items, 5, 3)).toEqual([]);
    });
  });

  describe('getPaginationInfo()', () => {
    const items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    it('should correctly calculate information for the first page', () => {
      const info = getPaginationInfo(items, { currentPage: 1, perPage: 3 });
      expect(info).toEqual({
        totalItems: 10,
        totalPages: 4,
        startItem: 1,
        endItem: 3
      });
    });

    it('should correctly calculate information for the second page', () => {
      const info = getPaginationInfo(items, { currentPage: 2, perPage: 3 });
      expect(info).toEqual({
        totalItems: 10,
        totalPages: 4,
        startItem: 4,
        endItem: 6
      });
    });

    it('should correctly calculate information for the last page', () => {
      const info = getPaginationInfo(items, { currentPage: 4, perPage: 3 });
      expect(info).toEqual({
        totalItems: 10,
        totalPages: 4,
        startItem: 10,
        endItem: 10
      });
    });
  });

  describe('__paginate()', () => {
    const items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    it('should return the correct page of items for the first page', () => {
      const state = {
        pagination: { currentPage: 1, perPage: 4 },
        filtered: items
      };
      expect(__paginate(state)).toEqual([1, 2, 3, 4]);
    });

    it('should return the correct page of items for the second page', () => {
      const state = {
        pagination: { currentPage: 2, perPage: 4 },
        filtered: items
      };
      expect(__paginate(state)).toEqual([5, 6, 7, 8]);
    });

    it('should return the correct items when the last page is incomplete', () => {
      const state = {
        pagination: { currentPage: 3, perPage: 4 },
        filtered: items
      };
      expect(__paginate(state)).toEqual([9, 10]);
    });
  });
});
