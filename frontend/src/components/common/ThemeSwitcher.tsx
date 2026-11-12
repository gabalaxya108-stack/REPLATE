import React, { useState, useRef, useEffect } from 'react';
import { useTheme, THEMES } from '../../context/ThemeContext';
import { Palette, Check } from 'lucide-react';

export const ThemeSwitcher: React.FC<{ compact?: boolean }> = ({ compact = false }) => {
  const { theme, setTheme } = useTheme();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const currentTheme = THEMES.find((t) => t.id === theme) || THEMES[0];

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative inline-block text-left" ref={dropdownRef}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Change application color theme"
        className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-border bg-surface text-ink-primary hover:border-forest hover:text-forest transition-colors shadow-xs text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-forest/30"
      >
        <span className="text-base leading-none">{currentTheme.icon}</span>
        {!compact && <span className="hidden md:inline">{currentTheme.name}</span>}
        <Palette className="w-3.5 h-3.5 text-ink-muted" />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-64 rounded-2xl border border-border bg-surface p-2 shadow-elevated z-50 animate-in fade-in zoom-in-95 duration-150">
          <div className="px-3 py-2 border-b border-border mb-1">
            <p className="text-xs font-bold text-ink-primary">Select Theme</p>
            <p className="text-[11px] text-ink-secondary">High-contrast accessibility verified</p>
          </div>

          <div className="space-y-1">
            {THEMES.map((opt) => {
              const isSelected = opt.id === theme;
              return (
                <button
                  key={opt.id}
                  onClick={() => {
                    setTheme(opt.id);
                    setIsOpen(false);
                  }}
                  className={`w-full flex items-center justify-between p-2.5 rounded-xl text-left transition-all text-xs ${
                    isSelected
                      ? 'bg-forest/10 text-forest font-bold border border-forest/20'
                      : 'text-ink-primary hover:bg-warm-muted'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-lg">{opt.icon}</span>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-ink-primary">{opt.name}</span>
                        <div
                          className="w-3 h-3 rounded-full border border-border"
                          style={{ backgroundColor: opt.accentColor }}
                          title={`Accent: ${opt.accentColor}`}
                        />
                      </div>
                      <p className="text-[10px] text-ink-secondary leading-tight mt-0.5 line-clamp-1">
                        {opt.tagline}
                      </p>
                    </div>
                  </div>
                  {isSelected && <Check className="w-4 h-4 text-forest flex-shrink-0" />}
                </button>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
