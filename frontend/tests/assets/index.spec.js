import { existsSync } from 'fs';
import path from 'path';
import { describe, it, expect } from 'vitest';

describe('Assets folder content', () => {
  const assetsPath = path.resolve(import.meta.dirname, '../../src/assets');

  console.log(assetsPath)

  // Seznam souborů, které by měly být ve složce assets.
  const expectedFiles = [
    'tailwind.css',
    'logo.svg',
    'logo.png',
    'base.css',
    'main.css'
  ];

  expectedFiles.forEach((file) => {
    it(`should contain ${file}`, () => {
      const filePath = path.join(assetsPath, file);
      expect(existsSync(filePath)).toBe(true);
    });
  });
});
