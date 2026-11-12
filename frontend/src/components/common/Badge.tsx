import React from 'react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'veg' | 'non-veg' | 'category' | 'available' | 'allocated' | 'warning' | 'neutral';
  size?: 'sm' | 'md';
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'neutral',
  size = 'sm',
  className,
}) => {
  const base = 'inline-flex items-center font-medium rounded-full';

  const variants = {
    veg: 'bg-emerald-500/15 text-emerald-800 dark:text-emerald-300 border border-emerald-500/30 font-semibold',
    'non-veg': 'bg-rose-500/15 text-rose-800 dark:text-rose-300 border border-rose-500/30 font-semibold',
    category: 'bg-forest/10 text-forest-dark border border-forest/20 font-semibold',
    available: 'bg-emerald-500/15 text-emerald-800 dark:text-emerald-300 border border-emerald-500/30 font-semibold',
    allocated: 'bg-amber-500/15 text-amber-900 dark:text-amber-300 border border-amber-500/30 font-semibold',
    warning: 'bg-orange-500/15 text-orange-900 dark:text-orange-300 border border-orange-500/30 font-semibold',
    neutral: 'bg-warm-muted text-ink-secondary border border-border font-medium',
  };

  const sizes = {
    sm: 'text-xs px-2.5 py-0.5 gap-1',
    md: 'text-sm px-3 py-1 gap-1.5',
  };

  return (
    <span className={twMerge(clsx(base, variants[variant], sizes[size], className))}>
      {variant === 'veg' && (
        <span className="w-1.5 h-1.5 rounded-full bg-emerald-600 mr-1" />
      )}
      {variant === 'non-veg' && (
        <span className="w-1.5 h-1.5 rounded-full bg-rose-600 mr-1" />
      )}
      {children}
    </span>
  );
};

