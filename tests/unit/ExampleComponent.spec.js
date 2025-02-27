import { mount } from '@vue/test-utils'
import ExampleComponent from '@/components/ExampleComponent.vue'

describe('ExampleComponent', () => {
  it('renders properly', () => {
    const wrapper = mount(ExampleComponent)
    expect(wrapper.exists()).toBe(true)
  })

  it('updates counter when button is clicked', async () => {
    const wrapper = mount(ExampleComponent)
    const button = wrapper.find('button')
    const countBefore = wrapper.vm.count
    
    await button.trigger('click')
    
    expect(wrapper.vm.count).toBe(countBefore + 1)
  })
}) 