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
    veg: 'bg-emerald-50 text-emerald-800 border border-emerald-200',
    'non-veg': 'bg-rose-50 text-rose-800 border border-rose-200',
    category: 'bg-forest-subtle text-forest-dark border border-forest/10',
    available: 'bg-emerald-50 text-emerald-700 border border-emerald-200',
    allocated: 'bg-amber-50 text-amber-800 border border-amber-200',
    warning: 'bg-orange-50 text-orange-800 border border-orange-200',
    neutral: 'bg-warm-muted text-ink-secondary border border-border',
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
