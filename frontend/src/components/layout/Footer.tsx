import React from 'react';
import { Link } from 'react-router-dom';
import { ThemeSwitcher } from '../common/ThemeSwitcher';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-surface border-t border-border mt-24">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10">
          {/* Brand Column */}
          <div className="lg:col-span-2 space-y-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-forest flex items-center justify-center text-white">
                <span className="font-bold text-lg">R</span>
              </div>
              <span className="font-extrabold text-2xl tracking-tight text-forest-dark">
                REPLATE
              </span>
            </div>
            <p className="text-sm text-ink-secondary leading-relaxed max-w-sm">
              An enterprise social-impact food recovery engine connecting commercial food establishments, verified NGOs, and volunteer networks across Indian metropolitan cities to eliminate avoidable food waste.
            </p>
            <div className="flex items-center gap-2 text-xs text-ink-muted">
              <span className="inline-block w-2 h-2 rounded-full bg-emerald-500" />
              <span>Academic Project: Object-Oriented Programming Using Java</span>
            </div>
          </div>

          {/* Solution Links */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-ink-primary">Platform</h4>
            <ul className="space-y-2 text-sm text-ink-secondary">
              <li><Link to="/explore" className="hover:text-forest transition-colors">Surplus Food Feed</Link></li>
              <li><a href="#how-it-works" className="hover:text-forest transition-colors">How Replate Works</a></li>
              <li><a href="#impact" className="hover:text-forest transition-colors">Environmental Impact</a></li>
              <li><a href="#network" className="hover:text-forest transition-colors">Partner Showcase</a></li>
            </ul>
          </div>

          {/* User Categories */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-ink-primary">Stakeholders</h4>
            <ul className="space-y-2 text-sm text-ink-secondary">
              <li><Link to="/register?role=DONOR" className="hover:text-forest transition-colors">Commercial Donors</Link></li>
              <li><Link to="/register?role=NGO" className="hover:text-forest transition-colors">Verified NGOs</Link></li>
              <li><Link to="/register?role=VOLUNTEER" className="hover:text-forest transition-colors">Volunteer Couriers</Link></li>
              <li><Link to="/login?role=ADMIN" className="hover:text-forest transition-colors">Admin Governance</Link></li>
            </ul>
          </div>

          {/* Architecture & Compliance */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-ink-primary">Compliance</h4>
            <p className="text-xs text-ink-secondary leading-relaxed">
              Designed according to FSSAI Safe Food Donation guidelines and NITI Aayog NGO Darpan registry protocols.
            </p>
            <div className="pt-2 flex flex-col gap-1 text-[11px] text-ink-muted">
              <span>Stack: Java 21 • Spring Boot 3.3 • PostgreSQL</span>
              <span>Architecture: Modular Clean Monolith</span>
            </div>
          </div>
        </div>

        {/* Horizontal Theme Switcher Palette Bar in Footer */}
        <div className="pt-8 mt-12 border-t border-border flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <span className="text-xs font-bold text-ink-primary">Theme Palette:</span>
            <ThemeSwitcher variant="footer" showLabel={false} />
          </div>
          <div className="text-xs text-ink-muted">
            Instant live preview across all routes & dashboards
          </div>
        </div>

        <div className="pt-6 mt-6 border-t border-border/60 flex flex-col sm:flex-row items-center justify-between text-xs text-ink-secondary gap-4">
          <p>© 2026 REPLATE — Food Waste Donation & Redistribution System. All rights reserved.</p>
          <div className="flex items-center gap-6">
            <span>Built with care in Bengaluru, India</span>
            <span>•</span>
            <span className="text-forest font-medium">Rescue Food. Restore Hope.</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
