import { paginate, getPaginationInfo, paginateViaState } from '@/utils'; // adjust the path as needed
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
      expect(paginateViaState(state)).toEqual([1, 2, 3, 4]);
    });

    it('should return the correct page of items for the second page', () => {
      const state = {
        pagination: { currentPage: 2, perPage: 4 },
        filtered: items
      };
      expect(paginateViaState(state)).toEqual([5, 6, 7, 8]);
    });

    it('should return the correct items when the last page is incomplete', () => {
      const state = {
        pagination: { currentPage: 3, perPage: 4 },
        filtered: items
      };
      expect(paginateViaState(state)).toEqual([9, 10]);
    });
  });
});

describe('Pagination Utilities', () => {
  describe('paginateViaState', () => {
    it('should return empty array when items is empty', () => {
      const state = {
        items: [],
        filtered: [],
        pagination: { currentPage: 1, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual([])
    })
    
    it('should return first page of items', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 1, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(0, 10))
    })
    
    it('should return second page of items', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 2, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(10, 15))
    })
    
    it('should return empty array when page is out of bounds', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 3, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual([])
    })
    
    it('should handle perPage = 0', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 1, perPage: 0 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual([])
    })
    
    it('should handle negative perPage', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 1, perPage: -5 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(0, 10))
    })
    
    it('should handle negative currentPage', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: -1, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(0, 5))
    })
    
    it('should handle currentPage = 0', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 0, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual([])
    })
    
    it('should handle exactly one page of items', () => {
      const items = Array.from({ length: 10 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 1, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items)
    })
    
    it('should handle partial last page', () => {
      const items = Array.from({ length: 13 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 2, perPage: 10 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(10, 13))
    })
    
    it('should handle small perPage value', () => {
      const items = Array.from({ length: 15 }, (_, i) => ({ id: i + 1 }))
      const state = {
        items,
        filtered: items,
        pagination: { currentPage: 3, perPage: 5 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual(items.slice(10, 15))
    })
    
    it('should handle filtered items array', () => {
      const state = {
        items: [
          { id: 1, active: true },
          { id: 2, active: false },
          { id: 3, active: true },
          { id: 4, active: false },
          { id: 5, active: true }
        ],
        filtered: [
          { id: 1, active: true },
          { id: 3, active: true },
          { id: 5, active: true }
        ],
        pagination: { currentPage: 1, perPage: 2 }
      }
      
      const result = paginateViaState(state)
      
      expect(result).toEqual([
        { id: 1, active: true },
        { id: 3, active: true }
      ])
    })
  })
})
