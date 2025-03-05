import { $reactive } from '@/utils'; // upravte cestu dle umístění souboru
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

    // Přidáme novou hodnotu a změníme hodnotu již existujícího klíče.
    obj.$assign({ a: 10, c: 3 });

    expect(obj.a).toBe(10); // stávající klíč byl přepsán
    expect(obj.b).toBe(2);  // zbývající klíč zůstává nezměněn
    expect(obj.c).toBe(3);  // nový klíč byl přidán
  });

  it('should reset values defined in default with $clear()', () => {
    const defaults = { a: 1, b: 2 };
    const obj = $reactive(defaults);

    // Změníme hodnoty a přidáme nový klíč, který není v default
    obj.$assign({ a: 10, b: 20, c: 3 });

    expect(obj.a).toBe(10);
    expect(obj.b).toBe(20);
    expect(obj.c).toBe(3);

    // Po zavolání $clear() by měly být hodnoty definované v default resetovány.
    obj.$clear();

    expect(obj.a).toBe(1);
    expect(obj.b).toBe(2);
    // Vzhledem k implementaci se dodatečný klíč (c) neodstraní.
    expect(obj.c).toBe(3);
  });
});
