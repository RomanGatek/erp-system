import { vi } from 'vitest'
//
// // __mocks__/auth.js
export const auth = {
  login: vi.fn(),
  register: vi.fn(),
  logout: vi.fn(),
  forgotPassword: vi.fn(),
  resetPassword: vi.fn()
}
