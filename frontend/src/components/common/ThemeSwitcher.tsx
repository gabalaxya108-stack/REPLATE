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
      aria-label="Color Theme Selector"
      className={`inline-flex flex-row items-center p-1 rounded-xl bg-surface border border-border shadow-xs transition-colors duration-200 ${className}`}
    >
      <span className="text-[10px] uppercase font-bold text-ink-muted tracking-wider px-2 hidden lg:inline select-none">
        Theme
      </span>
      <div className="flex flex-row items-center gap-1">
        {THEMES.map((opt) => {
          const isSelected = opt.id === theme;
          return (
            <button
              key={opt.id}
              type="button"
              onClick={() => setTheme(opt.id)}
              title={`${opt.name} — ${opt.tagline}`}
              aria-pressed={isSelected}
              className={`flex flex-row items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-semibold transition-all duration-200 cursor-pointer select-none ${
                isSelected
                  ? 'bg-forest text-white shadow-sm ring-1 ring-forest/30 scale-[1.02]'
                  : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
              }`}
            >
              <span className="text-sm leading-none shrink-0" aria-hidden="true">
                {opt.icon}
              </span>
              <span
                className={`whitespace-nowrap text-[11px] font-semibold tracking-tight ${
                  compactOnMobile ? 'hidden md:inline' : 'inline'
                }`}
              >
                {opt.name.split(' ')[0]}
              </span>
              <span
                className={`w-2 h-2 rounded-full shrink-0 border ${
                  isSelected ? 'border-white/60' : 'border-ink-muted/30'
                }`}
                style={{ backgroundColor: opt.accentColor }}
                aria-hidden="true"
              />
            </button>
          );
        })}
      </div>
    </div>
  );
};
