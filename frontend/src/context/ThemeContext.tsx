import React, { createContext, useContext, useEffect, useState } from 'react';

export type AppTheme = 'nature' | 'dark' | 'sunset' | 'ocean';

export interface ThemeOption {
  id: AppTheme;
  name: string;
  tagline: string;
  accentColor: string;
  bgColor: string;
  icon: string;
}

export const THEMES: ThemeOption[] = [
  {
    id: 'nature',
    name: 'Emerald Harvest',
    tagline: 'Fresh organic linen with deep forest emerald accents',
    accentColor: '#1E513F',
    bgColor: '#F3F6F2',
    icon: '🌿',
  },
  {
    id: 'dark',
    name: 'Midnight Pantry',
    tagline: 'Deep dark slate with high-contrast text & luminous emerald',
    accentColor: '#10B981',
    bgColor: '#0B1120',
    icon: '🌙',
  },
  {
    id: 'sunset',
    name: 'Royal Saffron',
    tagline: 'Warm golden amber and rich Indian heritage spice tones',
    accentColor: '#C2410C',
    bgColor: '#FFF7EB',
    icon: '🌅',
  },
  {
    id: 'ocean',
    name: 'Coastal Azure',
    tagline: 'Cool maritime blue and teal social impact',
    accentColor: '#0284C7',
    bgColor: '#EEF6FB',
    icon: '🌊',
  },
];

interface ThemeContextType {
  theme: AppTheme;
  setTheme: (theme: AppTheme) => void;
  availableThemes: ThemeOption[];
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

function applyThemeToDOM(activeTheme: AppTheme) {
  if (typeof document === 'undefined') return;

  const html = document.documentElement;
  const body = document.body;

  // 1. Data attribute
  html.setAttribute('data-theme', activeTheme);
  if (body) {
    body.setAttribute('data-theme', activeTheme);
  }

  // 2. Class-based dark mode for Tailwind
  if (activeTheme === 'dark') {
    html.classList.add('dark');
    if (body) body.classList.add('dark');
  } else {
    html.classList.remove('dark');
    if (body) body.classList.remove('dark');
  }

  // 3. Explicit theme class
  const allThemes: AppTheme[] = ['nature', 'dark', 'sunset', 'ocean'];
  allThemes.forEach((t) => {
    html.classList.remove(`theme-${t}`);
    if (body) body.classList.remove(`theme-${t}`);
  });
  html.classList.add(`theme-${activeTheme}`);
  if (body) body.classList.add(`theme-${activeTheme}`);
}

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [theme, setThemeState] = useState<AppTheme>(() => {
    try {
      const saved = localStorage.getItem('replate_theme') as AppTheme;
      if (saved && ['nature', 'dark', 'sunset', 'ocean'].includes(saved)) {
        return saved;
      }
    } catch {
      // Fallback
    }
    return 'nature';
  });

  const setTheme = (newTheme: AppTheme) => {
    setThemeState(newTheme);
    try {
      localStorage.setItem('replate_theme', newTheme);
    } catch {
      // Ignore storage errors
    }
    applyThemeToDOM(newTheme);
  };

  useEffect(() => {
    applyThemeToDOM(theme);
  }, [theme]);

  return (
    <ThemeContext.Provider value={{ theme, setTheme, availableThemes: THEMES }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};
