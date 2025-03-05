import { setActivePinia, createPinia } from 'pinia'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useNotifier } from '@/stores/notifier'
import { notify } from '@kyvg/vue3-notification'

// Mock the notify function so we can spy on its calls
vi.mock('@kyvg/vue3-notification', () => ({
  notify: vi.fn()
}));

describe('Notifier Store', () => {
  beforeEach(() => {
    // Set up a fresh Pinia instance before each test
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should trigger an info notification with default parameters', () => {
    const notifier = useNotifier()
    const message = 'Test info message'
    notifier.info(message)

    expect(notify).toHaveBeenCalledWith({
      title: 'Info',
      text: message,
      type: 'info',
      closeOnClick: true,
      duration: 5000,
      speed: 1000
    })
  })

  it('should trigger a success notification with custom parameters', () => {
    const notifier = useNotifier()
    const message = 'Test success message'
    notifier.success(message, 'Great', 3000, 500)

    expect(notify).toHaveBeenCalledWith({
      title: 'Great',
      text: message,
      type: 'success',
      closeOnClick: true,
      duration: 3000,
      speed: 500
    })
  })

  it('should trigger a warning notification with default parameters', () => {
    const notifier = useNotifier()
    const message = 'Test warning message'
    notifier.warning(message)

    expect(notify).toHaveBeenCalledWith({
      title: 'Warning',
      text: message,
      type: 'warn',
      closeOnClick: true,
      duration: 5000,
      speed: 1000
    })
  })

  it('should trigger an error notification with default parameters', () => {
    const notifier = useNotifier()
    const message = 'Test error message'
    notifier.error(message)

    expect(notify).toHaveBeenCalledWith({
      title: 'Error',
      text: message,
      type: 'error',
      closeOnClick: true,
      duration: 5000,
      speed: 1000
    })
  })
})
