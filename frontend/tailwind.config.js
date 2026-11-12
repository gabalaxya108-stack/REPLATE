/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        surface: 'rgb(var(--color-surface-rgb) / <alpha-value>)',
        forest: {
          DEFAULT: 'rgb(var(--color-forest-rgb) / <alpha-value>)',
          dark: 'var(--color-forest-dark)',
          light: 'var(--color-forest-light)',
          subtle: 'var(--color-forest-subtle)',
        },
        sage: {
          DEFAULT: 'rgb(var(--color-sage-rgb) / <alpha-value>)',
          light: 'var(--color-sage-light)',
          dark: 'var(--color-sage-dark)',
        },
        terracotta: {
          DEFAULT: 'rgb(var(--color-terracotta-rgb) / <alpha-value>)',
          light: 'var(--color-terracotta-light)',
          dark: 'var(--color-terracotta-dark)',
        },
        warm: {
          bg: 'rgb(var(--color-bg-rgb) / <alpha-value>)',
          surface: 'rgb(var(--color-surface-rgb) / <alpha-value>)',
          card: 'rgb(var(--color-card-rgb) / <alpha-value>)',
          muted: 'rgb(var(--color-muted-rgb) / <alpha-value>)',
        },
        ink: {
          primary: 'var(--color-ink-primary)',
          secondary: 'var(--color-ink-secondary)',
          muted: 'var(--color-ink-muted)',
        },
        border: {
          DEFAULT: 'rgb(var(--color-border-rgb) / <alpha-value>)',
          subtle: 'var(--color-border-subtle)',
          strong: 'var(--color-border-strong)',
        }
      },
      fontFamily: {
        sans: ['Inter', 'Plus Jakarta Sans', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'subtle': '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px -1px rgba(0, 0, 0, 0.05)',
        'card': '0 4px 20px -2px rgba(0, 0, 0, 0.06), 0 2px 6px -1px rgba(0, 0, 0, 0.04)',
        'elevated': '0 12px 32px -4px rgba(0, 0, 0, 0.08), 0 4px 12px -2px rgba(0, 0, 0, 0.04)',
      }
    },
  },
  plugins: [],
}
