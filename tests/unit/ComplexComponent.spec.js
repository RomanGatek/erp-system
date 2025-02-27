import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import ComplexComponent from '@/components/ComplexComponent.vue'

describe('ComplexComponent', () => {
  let store

  beforeEach(() => {
    store = createStore({
      state: {
        items: []
      },
      mutations: {
        addItem(state, item) {
          state.items.push(item)
        }
      }
    })
  })

  it('interacts with Vuex store', async () => {
    const wrapper = mount(ComplexComponent, {
      global: {
        plugins: [store]
      }
    })

    await wrapper.find('input').setValue('New Item')
    await wrapper.find('form').trigger('submit')

    expect(store.state.items).toContain('New Item')
  })
}) 