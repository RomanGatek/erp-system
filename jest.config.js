module.exports = {
  moduleFileExtensions: ['js', 'jsx', 'json', 'vue'],
  transform: {
    '^.+\\.vue$': '@vue/vue3-jest',
    '^.+\\.(js|jsx)?$': 'babel-jest'
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1'
  },
  testMatch: [
    '<rootDir>/tests/unit/**/*.spec.(js|jsx|ts|tsx)',
    '<rootDir>/**/__tests__/*.(js|jsx|ts|tsx)'
  ],
  testEnvironment: 'jsdom'
} 