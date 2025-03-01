<template>
  <div class="w-full flex items-center justify-center">
    <transition
      name="section"
      mode="out-in"
    >
      <!-- Hero Section -->
      <div v-if="currentSection === 'hero'" key="hero" class="text-center max-w-4xl mx-auto px-4">
        <h1 class="text-gradient text-6xl font-extrabold mb-8 tracking-tight leading-tight">
          Welcome to the<br>
          <span class="gradient-text">ERP System</span>
        </h1>
        <p class="text-xl text-gray-600 max-w-2xl mx-auto leading-relaxed mb-12">
          Manage your users, products, and inventory efficiently with our modern and intuitive interface.
        </p>
        <div class="flex gap-4 justify-center">
          <button
            @click="currentSection = 'team'"
            class="group px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-400 text-white rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden"
          >
            <span class="relative z-10">Who Are We?</span>
            <div class="absolute inset-0 bg-gradient-to-r from-blue-500 to-blue-300 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </button>
          <button
            @click="currentSection = 'about'"
            class="group px-6 py-3 bg-white/15 backdrop-blur-lg text-gray-700 rounded-xl font-medium transition-all duration-300 hover:-translate-y-0.5 relative overflow-hidden"
          >
            <span class="relative z-10">About Course</span>
            <div class="absolute inset-0 bg-gradient-to-r from-gray-500/20 to-white/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </button>
        </div>
      </div>

      <!-- Team Section -->
      <TeamView
        v-else-if="currentSection === 'team'"
        key="team"
        :team-members="teamMembers"
        @change-section="currentSection = $event"
      />

      <!-- About Course Section -->
      <CourseView
        v-else
        key="about"
        @change-section="currentSection = $event"
      />
    </transition>
  </div>
</template>

<script>
import TeamView from '@/components/TeamView.vue'
import CourseView from '@/components/CourseView.vue'

export default {
  name: 'HomeView',
  components: {
    TeamView,
    CourseView
  },
  data() {
    return {
      currentSection: 'hero',
      teamMembers: [
        {
          name: 'Jiří Grygerek',
          role: 'Lead Developer',
          github: 'https://github.com/cooffeeRequired',
        },
        {
          name: 'Štefan Pócsik',
          role: 'Java Developer',
          github: 'https://github.com/steve25'
        },
        {
          name: 'Matěj Chaloupka',
          role: 'Java Developer'
        },
        {
          name: 'Michal Rostislav Rabinoský',
          role: 'Java Developer',
          github: 'https://github.com/Michal-30'
        },
        {
          name: 'Rastislav Dorčák',
          role: 'Java Developer',
          github: 'https://github.com/Rostja'
        },
        {
          name: 'Roman Gaťek',
          role: 'Java Developer',
          github: 'https://github.com/RomanGatek'
        },
        {
          name: 'Timon Arvay',
          role: 'Java Developer',
          github: 'https://github.com/0tiy'
        },
        {
          name: 'Christos Stefanakis',
          role: 'Java Developer',
          github: 'https://github.com/cstefanakis'
        }
      ]
    }
  },
  watch: {
    '$route.query.section': {
      immediate: true,
      handler(newSection) {
        if (newSection) {
          this.currentSection = newSection
          setTimeout(() => {
            window.scrollTo({
              top: 0,
              behavior: 'smooth'
            })
          }, 100)
        } else {
          this.currentSection = 'hero'
        }
      }
    }
  },
  methods: {
    getInitials(name) {
      return name
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
    }
  }
}
</script>

<style scoped>
.h-full {
  min-height: calc(100vh - 4rem); /* Upravíme výšku */
}

.text-gradient {
  background: linear-gradient(
    135deg,
    #1e40af 0%,
    #3b82f6 25%,
    #2563eb 50%,
    #1d4ed8 75%,
    #1e40af 100%
  );
  background-size: 400% 400%;
  animation: gradient 6s ease infinite;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.gradient-text {
  background: linear-gradient(
    to right,
    #3b82f6,
    #2563eb,
    #1d4ed8
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* Přidáme efekt pro tlačítka */
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
  100% {
    transform: translateY(0px);
  }
}

.router-link-active {
  animation: float 3s ease-in-out infinite;
}

/* Přidáme nové styly pro karty */
.grid {
  perspective: 1000px;
  margin: 0 auto;
  max-width: 1200px; /* Omezení maximální šířky gridu */
}

.bg-white {
  transform-style: preserve-3d;
  backface-visibility: hidden;
}

/* Vylepšený hover efekt pro karty - jemnější transformace */
.bg-white:hover {
  transform: translateY(-3px) rotateX(3deg);
  box-shadow:
    0 15px 20px -5px rgba(0, 0, 0, 0.1),
    0 8px 8px -5px rgba(0, 0, 0, 0.04),
    0 0 0 1px rgba(59, 130, 246, 0.1);
}

/* Animace pro přechod mezi sekcemi */
.section-enter-active,
.section-leave-active {
  transition: all 0.5s ease;
}

.section-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.section-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

/* Vylepšení pro karty - postupné objevování */
.grid > div {
  animation: cardAppear 0.6s ease backwards;
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Aplikujeme zpoždění pro každou kartu */
.grid > div:nth-child(1) { animation-delay: 0.1s; }
.grid > div:nth-child(2) { animation-delay: 0.2s; }
.grid > div:nth-child(3) { animation-delay: 0.3s; }
.grid > div:nth-child(4) { animation-delay: 0.4s; }
.grid > div:nth-child(5) { animation-delay: 0.5s; }
.grid > div:nth-child(6) { animation-delay: 0.6s; }
.grid > div:nth-child(7) { animation-delay: 0.7s; }
.grid > div:nth-child(8) { animation-delay: 0.8s; }

/* Vylepšení pro text */
.text-gradient, .gradient-text {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Přidáme animaci pro statistiky */
.grid > div {
  animation: fadeInUp 0.6s ease backwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Aplikujeme zpoždění pro statistiky */
.grid > div:nth-child(1) { animation-delay: 0.2s; }
.grid > div:nth-child(2) { animation-delay: 0.4s; }
.grid > div:nth-child(3) { animation-delay: 0.6s; }

/* Přidáme smooth scroll pro celou stránku */
:root {
  scroll-behavior: smooth;
}

/* Zajistíme, že obsah bude vždy viditelný */
.section-enter-active,
.section-leave-active {
  position: absolute;
  width: 100%;
}
</style>
