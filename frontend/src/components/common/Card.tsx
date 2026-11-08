import React from 'react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  hoverEffect?: boolean;
  padding?: 'none' | 'sm' | 'md' | 'lg';
}

export const Card: React.FC<CardProps> = ({
  children,
  hoverEffect = false,
  padding = 'md',
  className,
  ...props
}) => {
  const paddings = {
    none: 'p-0',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  return (
    <div
      className={twMerge(
        clsx(
          'bg-surface rounded-xl border border-border shadow-card transition-all duration-200',
          hoverEffect && 'hover:shadow-elevated hover:border-sage hover:-translate-y-0.5',
          paddings[padding],
          className
        )
      )}
      {...props}
    >
      {children}
    </div>
  );
};
