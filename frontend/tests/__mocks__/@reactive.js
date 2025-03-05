import { vi } from 'vitest'
import { $reactive } from '@/utils/index.js'

export const __$reactive__ = vi.fn($reactive);
