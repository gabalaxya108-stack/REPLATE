import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../common/Button';
import { Menu, X, ArrowUpRight } from 'lucide-react';

export const Navbar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);

  const navLinks = [
    { label: 'Live Food Feed', path: '/explore' },
    { label: 'How Replate Works', path: '/#how-it-works' },
    { label: 'Impact Metrics', path: '/#impact' },
    { label: 'Partner Network', path: '/#network' },
  ];

  return (
    <header className="sticky top-0 z-50 bg-warm-bg/95 backdrop-blur-md border-b border-border transition-all">
      {/* Top Banner: Academic & FSSAI Trust Anchor */}
      <div className="bg-forest-subtle py-1.5 px-4 text-xs text-forest-dark border-b border-border/60">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="inline-block w-2 h-2 rounded-full bg-emerald-600 animate-pulse" />
            <span className="font-medium">Surplus Food Recovery Platform • Aligned with FSSAI & NITI Aayog Darpan Guidelines</span>
          </div>
          <div className="hidden md:flex items-center gap-4 text-ink-secondary text-[11px]">
            <span>Bengaluru</span>
            <span>•</span>
            <span>Delhi NCR</span>
            <span>•</span>
            <span>Mumbai</span>
            <span>•</span>
            <span>Hyderabad</span>
          </div>
        </div>
      </div>

      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
        {/* Brand Identity */}
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-11 h-11 rounded-xl bg-forest flex items-center justify-center text-white shadow-sm group-hover:bg-forest-dark transition-colors">
            {/* Custom stylized plate/leaf icon */}
            <svg className="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 2C6.477 2 2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.891h-2.33v6.988C18.343 21.128 22 16.991 22 12c0-5.523-4.477-10-10-10z" className="opacity-0 hidden" />
              <circle cx="12" cy="12" r="9" strokeWidth="2" />
              <path d="M12 7c-2.76 0-5 2.24-5 5 0 2.2 1.43 4.07 3.42 4.71.5.09.83-.22.83-.48v-1.7" />
              <path d="M14.5 10c0-1.38-1.12-2.5-2.5-2.5" />
              <circle cx="12" cy="12" r="2" fill="currentColor" />
            </svg>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-extrabold text-2xl tracking-tight text-forest-dark font-sans">
                REPLATE
              </span>
              <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-sage/30 text-forest-dark border border-sage/40">
                India
              </span>
            </div>
            <p className="text-[11px] font-medium text-ink-secondary tracking-wide">
              Rescue Food. Restore Hope.
            </p>
          </div>
        </Link>

        {/* Desktop Navigation Links */}
        <div className="hidden lg:flex items-center gap-8">
          {navLinks.map((link) => (
            <Link
              key={link.path}
              to={link.path}
              className="text-sm font-medium text-ink-primary hover:text-forest transition-colors relative py-1 group"
            >
              {link.label}
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-forest transition-all duration-200 group-hover:w-full" />
            </Link>
          ))}
        </div>

        {/* CTA Actions */}
        <div className="hidden sm:flex items-center gap-3">
          <Link to="/login">
            <Button variant="ghost" size="sm" className="font-medium">
              Sign In
            </Button>
          </Link>
          <Link to="/register">
            <Button variant="primary" size="sm" rightIcon={<ArrowUpRight className="w-4 h-4" />}>
              Get Started
            </Button>
          </Link>
        </div>

        {/* Mobile menu button */}
        <div className="lg:hidden flex items-center">
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="p-2 rounded-lg text-ink-primary hover:bg-warm-muted focus:outline-none"
            aria-label="Toggle Menu"
          >
            {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </nav>

      {/* Mobile Drawer */}
      {isOpen && (
        <div className="lg:hidden bg-surface border-b border-border px-4 pt-3 pb-6 space-y-3 animate-in fade-in slide-in-from-top-2 duration-150">
          <div className="flex flex-col space-y-2">
            {navLinks.map((link) => (
              <Link
                key={link.path}
                to={link.path}
                onClick={() => setIsOpen(false)}
                className="px-3 py-2.5 rounded-lg text-sm font-medium text-ink-primary hover:bg-warm-muted hover:text-forest"
              >
                {link.label}
              </Link>
            ))}
          </div>
          <div className="pt-3 border-t border-border flex flex-col gap-2">
            <Link to="/login" onClick={() => setIsOpen(false)} className="w-full">
              <Button variant="outline" className="w-full justify-center">
                Sign In
              </Button>
            </Link>
            <Link to="/register" onClick={() => setIsOpen(false)} className="w-full">
              <Button variant="primary" className="w-full justify-center">
                Get Started
              </Button>
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};
