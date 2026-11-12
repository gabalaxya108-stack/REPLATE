import React from 'react';
import { useTheme, THEMES, AppTheme } from '../../context/ThemeContext';

interface ThemeSwitcherProps {
  className?: string;
  variant?: 'bar' | 'floating' | 'footer';
  showLabel?: boolean;
}

export const ThemeSwitcher: React.FC<ThemeSwitcherProps> = ({
  className = '',
  variant = 'bar',
  showLabel = true,
}) => {
  const { theme, setTheme } = useTheme();

  return (
    <div
      role="group"
      aria-label="Website Theme Palette"
      className={`inline-flex flex-row items-center gap-1 p-1 rounded-xl transition-all duration-200 select-none ${
        variant === 'floating'
          ? 'bg-surface/95 backdrop-blur-md border-2 border-forest/30 shadow-elevated'
          : variant === 'footer'
          ? 'bg-surface border border-border shadow-xs'
          : 'bg-surface/90 border border-border shadow-xs'
      } ${className}`}
    >
      {showLabel && (
        <span className="text-[10px] uppercase font-extrabold text-ink-muted tracking-wider px-2 hidden sm:inline">
          Theme:
        </span>
      )}
      <div className="flex flex-row items-center gap-1 overflow-x-auto scrollbar-none">
        {THEMES.map((opt) => {
          const isSelected = opt.id === theme;
          return (
            <button
              key={opt.id}
              type="button"
              onClick={() => setTheme(opt.id as AppTheme)}
              title={`${opt.name}: ${opt.tagline}`}
              aria-pressed={isSelected}
              className={`flex flex-row items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-bold transition-all duration-150 cursor-pointer ${
                isSelected
                  ? 'bg-forest text-white shadow-sm ring-1 ring-forest/40 scale-[1.03]'
                  : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
              }`}
            >
              <span className="text-sm leading-none shrink-0" aria-hidden="true">
                {opt.icon}
              </span>
              <span className="whitespace-nowrap text-[11px] font-semibold">
                {opt.name.split(' ')[0]}
              </span>
              <span
                className={`w-2 h-2 rounded-full shrink-0 border ${
                  isSelected ? 'border-white/80' : 'border-ink-muted/40'
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
