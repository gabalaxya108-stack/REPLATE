import React from 'react';
import { useTheme, THEMES } from '../../context/ThemeContext';

interface ThemeSwitcherProps {
  className?: string;
  compactOnMobile?: boolean;
}

export const ThemeSwitcher: React.FC<ThemeSwitcherProps> = ({
  className = '',
  compactOnMobile = false,
}) => {
  const { theme, setTheme } = useTheme();

  return (
    <div
      role="group"
      aria-label="Theme selector"
      className={`inline-flex items-center p-1 rounded-xl bg-surface/90 border border-border shadow-xs backdrop-blur-sm ${className}`}
    >
      <span className="text-[10px] uppercase font-bold text-ink-muted tracking-wider px-2 hidden xl:inline">
        Theme:
      </span>
      <div className="flex items-center gap-1 overflow-x-auto scrollbar-none">
        {THEMES.map((opt) => {
          const isSelected = opt.id === theme;
          return (
            <button
              key={opt.id}
              type="button"
              onClick={() => setTheme(opt.id)}
              title={`${opt.name}: ${opt.tagline}`}
              aria-pressed={isSelected}
              className={`flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-semibold transition-all duration-200 cursor-pointer ${
                isSelected
                  ? 'bg-forest text-white shadow-xs scale-[1.02]'
                  : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
              }`}
            >
              <span className="text-sm leading-none">{opt.icon}</span>
              <span className={`whitespace-nowrap text-[11px] font-semibold ${compactOnMobile ? 'hidden sm:inline' : 'inline'}`}>
                {opt.name.split(' ')[0]}
              </span>
              <span
                className="w-2 h-2 rounded-full border border-white/40 shrink-0"
                style={{ backgroundColor: opt.accentColor }}
              />
            </button>
          );
        })}
      </div>
    </div>
  );
};
