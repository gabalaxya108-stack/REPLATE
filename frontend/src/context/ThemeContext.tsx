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
    tagline: 'Warm organic linen with forest green accents',
    accentColor: '#245C45',
    bgColor: '#F8F8F3',
    icon: '🌿',
  },
  {
    id: 'dark',
    name: 'Midnight Pantry',
    tagline: 'Deep dark slate with high-contrast text',
    accentColor: '#10B981',
    bgColor: '#0B1120',
    icon: '🌙',
  },
  {
    id: 'sunset',
    name: 'Royal Saffron',
    tagline: 'Warm amber and rich heritage spice tones',
    accentColor: '#C2410C',
    bgColor: '#FFFDF7',
    icon: '🌅',
  },
  {
    id: 'ocean',
    name: 'Coastal Azure',
    tagline: 'Cool maritime blue and teal social impact',
    accentColor: '#0284C7',
    bgColor: '#F4F8FA',
    icon: '🌊',
  },
];

interface ThemeContextType {
  theme: AppTheme;
  setTheme: (theme: AppTheme) => void;
  availableThemes: ThemeOption[];
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [theme, setThemeState] = useState<AppTheme>(() => {
    try {
      const saved = localStorage.getItem('replate_theme') as AppTheme;
      if (saved && ['nature', 'dark', 'sunset', 'ocean'].includes(saved)) {
        return saved;
      }
    } catch {
      // Fallback to default
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
  };

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
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
