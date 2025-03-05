import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import HomeView from '@/views/Home.vue'

// Stub components for TeamView and CourseView
const TeamViewStub = {
  template: '<div class="team-view-stub">Team View</div>',
  props: ['teamMembers']
}

const CourseViewStub = {
  template: '<div class="course-view-stub">Course View</div>'
}

describe('HomeView.vue', () => {
  it('renders the hero section by default', () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          TeamView: TeamViewStub,
          CourseView: CourseViewStub
        }
      }
    })

    // Expect hero section content to be rendered
    expect(wrapper.html()).toContain('Welcome to the')
    // Ensure neither TeamView nor CourseView is visible
    expect(wrapper.find('.team-view-stub').exists()).toBe(false)
    expect(wrapper.find('.course-view-stub').exists()).toBe(false)
  })

  it('switches to the team section when "Who Are We?" button is clicked', async () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          TeamView: TeamViewStub,
          CourseView: CourseViewStub
        }
      }
    })

    // Find the button that contains "Who Are We?"
    const buttons = wrapper.findAll('button')
    const teamButton = buttons.find(btn => btn.text().includes('Who Are We?'))
    expect(teamButton).toBeDefined()
    await teamButton.trigger('click')

    // Verify that the component's currentSection is now 'team'
    expect(wrapper.vm.currentSection).toBe('team')
    await wrapper.vm.$nextTick()

    // Check that the TeamView stub is rendered
    expect(wrapper.find('.team-view-stub').exists()).toBe(true)
  })

  it('switches to the about section when "About Course" button is clicked', async () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          TeamView: TeamViewStub,
          CourseView: CourseViewStub
        }
      }
    })

    // Find the button that contains "About Course"
    const buttons = wrapper.findAll('button')
    const aboutButton = buttons.find(btn => btn.text().includes('About Course'))
    expect(aboutButton).toBeDefined()
    await aboutButton.trigger('click')

    // Verify that the component's currentSection is now 'about'
    expect(wrapper.vm.currentSection).toBe('about')
    await wrapper.vm.$nextTick()

    // Check that the CourseView stub is rendered
    expect(wrapper.find('.course-view-stub').exists()).toBe(true)
  })
})
