module.exports = {
    content: ['./src/**/*.{vue,js,ts}'],
    safelist: [
        {
          pattern: /(bg|text|ring)-[a-z]+-\d{1,3}(\/\d{1,3})?/,
        },
      ],
}