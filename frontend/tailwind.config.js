/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        forest: {
          DEFAULT: '#245C45',
          dark: '#1C4836',
          light: '#2E7558',
          subtle: '#E9F0EC',
        },
        sage: {
          DEFAULT: '#A8BFA8',
          light: '#D3E0D3',
          dark: '#7D9C7D',
        },
        terracotta: {
          DEFAULT: '#D98A68',
          light: '#F4D4C7',
          dark: '#BA6D4D',
        },
        warm: {
          bg: '#F8F8F3',
          surface: '#FFFFFF',
          card: '#FCFCF9',
          muted: '#F0F1EB',
        },
        ink: {
          primary: '#26332D',
          secondary: '#77847C',
          muted: '#9DA8A1',
        },
        border: {
          DEFAULT: '#E7EAE4',
          subtle: '#F0F2EE',
          strong: '#D3D8CF',
        }
      },
      fontFamily: {
        sans: ['Inter', 'Plus Jakarta Sans', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'subtle': '0 1px 3px 0 rgba(38, 51, 45, 0.05), 0 1px 2px -1px rgba(38, 51, 45, 0.05)',
        'card': '0 4px 20px -2px rgba(36, 92, 69, 0.06), 0 2px 6px -1px rgba(36, 92, 69, 0.04)',
        'elevated': '0 12px 32px -4px rgba(36, 92, 69, 0.08), 0 4px 12px -2px rgba(36, 92, 69, 0.04)',
      }
    },
  },
  plugins: [],
}

