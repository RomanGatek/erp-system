import { $reactive } from '@/utils'; // adjust the path as needed
import { describe, it, expect } from 'vitest';

describe('$reactive helper', () => {
  it('should initialize with default values', () => {
    const defaults = { a: 1, b: 2 };
    const obj = $reactive(defaults);

    expect(obj.a).toBe(1);
    expect(obj.b).toBe(2);
  });

  it('should merge new properties with $assign()', () => {
    const defaults = { a: 1, b: 2 };
    const obj = $reactive(defaults);

    // Assign a new value for an existing key and add a new key.
    obj.$assign({ a: 10, c: 3 });

    expect(obj.a).toBe(10); // existing key updated
    expect(obj.b).toBe(2);  // untouched key remains unchanged
    expect(obj.c).toBe(3);  // new key added
  });

  it('should reset default properties with $clear()', () => {
    const defaults = { a: 1, b: 2 };
    const obj = $reactive(defaults);

    // Change values and add a new property that is not in defaults.
    obj.$assign({ a: 10, b: 20, c: 3 });

    expect(obj.a).toBe(10);
    expect(obj.b).toBe(20);
    expect(obj.c).toBe(3);

    // Calling $clear() should reset properties defined in defaults.
    obj.$clear();

    expect(obj.a).toBe(1);
    expect(obj.b).toBe(2);
    // Extra properties not defined in defaults are not removed.
    expect(obj.c).toBe(3);
  });

  it('should handle multiple $assign() calls without altering the original defaults', () => {
    const defaults = { a: 1, b: 2 };
    const obj = $reactive(defaults);

    obj.$assign({ a: 5 });
    expect(obj.a).toBe(5);

    obj.$assign({ b: 10 });
    expect(obj.b).toBe(10);

    // After resetting, default values should be restored.
    obj.$clear();
    expect(obj.a).toBe(1);
    expect(obj.b).toBe(2);
  });

  it('should handle nested objects correctly (shallow copy)', () => {
    const defaults = { nested: { x: 1, y: 2 } };
    const obj = $reactive(defaults);

    // Update the nested object via $assign (it replaces the nested object entirely).
    obj.$assign({ nested: { x: 10, y: 20 } });
    expect(obj.nested.x).toBe(10);
    expect(obj.nested.y).toBe(20);

    // After calling $clear(), the nested object should revert to the original defaults.
    obj.$clear();
    expect(obj.nested.x).toBe(1);
    expect(obj.nested.y).toBe(2);
  });

  it('should maintain additional properties after $clear() even if modified', () => {
    const defaults = { a: 1 };
    const obj = $reactive(defaults);

    obj.$assign({ b: 2 });
    expect(obj.b).toBe(2);

    // Modify the additional property.
    obj.b = 5;
    expect(obj.b).toBe(5);

    // $clear() should only reset the default properties, leaving extra keys intact.
    obj.$clear();
    expect(obj.a).toBe(1);
    expect(obj.b).toBe(5);
  });

  it('should simulate a complex reactive state management scenario', () => {
    const defaults = { counter: 0, user: { name: 'Alice', loggedIn: false } };
    const state = $reactive(defaults);

    // Initial state checks.
    expect(state.counter).toBe(0);
    expect(state.user.name).toBe('Alice');
    expect(state.user.loggedIn).toBe(false);

    // Update state with $assign.
    state.$assign({ counter: state.counter + 1, user: { name: 'Bob', loggedIn: true } });
    expect(state.counter).toBe(1);
    expect(state.user.name).toBe('Bob');
    expect(state.user.loggedIn).toBe(true);

    // Further update state.
    state.$assign({ counter: state.counter + 5 });
    expect(state.counter).toBe(6);

    // Reset state; $clear should restore the defaults for properties defined in the initial defaults.
    state.$clear();
    expect(state.counter).toBe(0);
    expect(state.user.name).toBe('Alice');
    expect(state.user.loggedIn).toBe(false);
  });
});
