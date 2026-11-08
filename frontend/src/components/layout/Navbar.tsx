import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '../common/Button';
import { useAuth } from '../../context/AuthContext';
import { Menu, X, ArrowUpRight, LogOut, User as UserIcon } from 'lucide-react';

export const Navbar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const navLinks = [
    { label: 'Live Food Feed', path: '/explore' },
    { label: 'How Replate Works', path: '/#how-it-works' },
    { label: 'Impact Metrics', path: '/#impact' },
    { label: 'Partner Network', path: '/#network' },
  ];

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsOpen(false);
  };

  const getRoleBadgeColor = (role?: string) => {
    switch (role) {
      case 'ADMIN':
        return 'bg-amber-100 text-amber-800 border-amber-300';
      case 'DONOR':
        return 'bg-emerald-100 text-emerald-800 border-emerald-300';
      case 'NGO':
        return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'VOLUNTEER':
        return 'bg-purple-100 text-purple-800 border-purple-300';
      default:
        return 'bg-sage/30 text-forest-dark border-sage/40';
    }
  };

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

        {/* CTA Actions or Authenticated User Profile */}
        <div className="hidden sm:flex items-center gap-3">
          {isAuthenticated && user ? (
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2 bg-surface px-3 py-1.5 rounded-xl border border-border shadow-xs">
                <div className="w-8 h-8 rounded-lg bg-forest/10 text-forest flex items-center justify-center font-bold text-xs">
                  {user.fullName ? user.fullName.charAt(0).toUpperCase() : <UserIcon className="w-4 h-4" />}
                </div>
                <div className="text-left">
                  <div className="flex items-center gap-1.5">
                    <span className="text-xs font-bold text-ink-primary truncate max-w-[120px]">
                      {user.fullName}
                    </span>
                    <span className={`text-[9px] font-extrabold uppercase px-1.5 py-0.2 rounded border ${getRoleBadgeColor(user.role)}`}>
                      {user.role}
                    </span>
                  </div>
                  <span className="text-[10px] text-ink-secondary block">
                    {user.city || 'Verified User'}
                  </span>
                </div>
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={handleLogout}
                className="text-red-600 hover:text-red-700 hover:bg-red-50 flex items-center gap-1.5 font-medium"
              >
                <LogOut className="w-3.5 h-3.5" />
                Sign Out
              </Button>
            </div>
          ) : (
            <>
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
            </>
          )}
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
            {isAuthenticated && user ? (
              <div className="space-y-3">
                <div className="flex items-center gap-2 p-2 bg-warm-muted rounded-lg">
                  <div className="w-8 h-8 rounded-lg bg-forest text-white flex items-center justify-center font-bold text-xs">
                    {user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
                  </div>
                  <div>
                    <div className="text-xs font-bold text-ink-primary">{user.fullName}</div>
                    <div className="text-[10px] text-ink-secondary">{user.email} • {user.role}</div>
                  </div>
                </div>
                <Button variant="outline" className="w-full justify-center text-red-600 hover:bg-red-50" onClick={handleLogout}>
                  <LogOut className="w-4 h-4 mr-1.5" />
                  Sign Out
                </Button>
              </div>
            ) : (
              <>
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
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
};
