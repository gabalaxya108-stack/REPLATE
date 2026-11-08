import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import { ArrowRight, ShieldCheck, MapPin } from 'lucide-react';

export const HeroSection: React.FC = () => {
  return (
    <section className="relative overflow-hidden pt-12 pb-20 lg:pt-20 lg:pb-28">
      {/* Subtle organic background accent */}
      <div className="absolute top-0 right-1/4 w-96 h-96 bg-sage/15 rounded-full blur-3xl pointer-events-none -z-10" />
      <div className="absolute bottom-10 left-10 w-80 h-80 bg-terracotta/10 rounded-full blur-3xl pointer-events-none -z-10" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          {/* Left Column: Messaging & CTAs */}
          <div className="lg:col-span-7 space-y-8">
            {/* Pill Header */}
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-forest-subtle border border-forest/15 text-forest-dark text-xs font-semibold tracking-wide shadow-subtle">
              <span className="w-2 h-2 rounded-full bg-emerald-600 animate-pulse" />
              <span>Rescue Food. Restore Hope.</span>
              <span className="text-ink-muted">|</span>
              <span className="text-ink-secondary">India's Food Recovery Network</span>
            </div>

            {/* Main Headline */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-ink-primary tracking-tight leading-[1.15]">
              Every banquet excess can feed a community in need.
            </h1>

            {/* Sub-headline */}
            <p className="text-lg text-ink-secondary leading-relaxed max-w-2xl font-normal">
              REPLATE connects India's restaurants, hotel banquets, wedding halls, and corporate cafeterias with verified NGOs and volunteer couriers. Real-time matching, verified OTP handover, and zero food waste.
            </p>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3.5 pt-2">
              <Link to="/register?role=DONOR">
                <Button variant="primary" size="lg" className="w-full sm:w-auto shadow-sm" rightIcon={<ArrowRight className="w-4 h-4" />}>
                  Donate Surplus Food
                </Button>
              </Link>
              <Link to="/explore">
                <Button variant="outline" size="lg" className="w-full sm:w-auto">
                  Browse Live Food Feed
                </Button>
              </Link>
            </div>

            {/* Trust Points */}
            <div className="grid grid-cols-3 gap-6 pt-6 border-t border-border">
              <div className="space-y-1">
                <div className="text-2xl font-bold text-forest-dark">68M+</div>
                <div className="text-xs text-ink-secondary leading-snug">Tonnes wasted annually in India</div>
              </div>
              <div className="space-y-1">
                <div className="text-2xl font-bold text-forest-dark">&lt; 45 mins</div>
                <div className="text-xs text-ink-secondary leading-snug">Average volunteer dispatch time</div>
              </div>
              <div className="space-y-1">
                <div className="text-2xl font-bold text-forest-dark">100%</div>
                <div className="text-xs text-ink-secondary leading-snug">FSSAI & NGO Darpan verified</div>
              </div>
            </div>
          </div>

          {/* Right Column: Editorial Transaction Preview Card */}
          <div className="lg:col-span-5">
            <div className="relative mx-auto max-w-md lg:max-w-none">
              {/* Backing decorative frame */}
              <div className="absolute inset-0 bg-sage/20 rounded-2xl transform rotate-2 scale-[1.02] -z-10 transition-transform" />

              <div className="bg-surface rounded-2xl border border-border shadow-elevated p-6 space-y-6">
                {/* Card Header: Live Dispatch Status */}
                <div className="flex items-center justify-between pb-4 border-b border-border">
                  <div className="flex items-center gap-3">
                    <div className="w-3 h-3 rounded-full bg-emerald-500 animate-ping" />
                    <div>
                      <span className="text-xs font-bold text-ink-primary uppercase tracking-wider block">Live Redistribution</span>
                      <span className="text-[11px] text-ink-secondary">Bengaluru Urban Cluster #BLR-402</span>
                    </div>
                  </div>
                  <Badge variant="available">In Transit (70%)</Badge>
                </div>

                {/* Donation Snapshot */}
                <div className="space-y-3">
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="font-bold text-ink-primary text-base">
                        Fresh Veg Pulao & Mixed Veg Kurma
                      </h3>
                      <p className="text-xs text-ink-secondary mt-0.5 flex items-center gap-1.5">
                        <MapPin className="w-3.5 h-3.5 text-forest" />
                        <span>Saravana Bhavan Foods (Indiranagar)</span>
                      </p>
                    </div>
                    <Badge variant="veg">Pure Veg</Badge>
                  </div>

                  <div className="grid grid-cols-2 gap-2 bg-warm-muted/60 p-3 rounded-lg text-xs">
                    <div>
                      <span className="text-ink-secondary block text-[11px]">Quantity Diverted</span>
                      <span className="font-semibold text-ink-primary">50 Meal Boxes (Thalis)</span>
                    </div>
                    <div>
                      <span className="text-ink-secondary block text-[11px]">Thermal Status</span>
                      <span className="font-semibold text-emerald-700">Hot Insulated (65°C)</span>
                    </div>
                  </div>
                </div>

                {/* Redistribution Route */}
                <div className="space-y-3 pt-2">
                  <div className="relative pl-6 space-y-4 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-sage">
                    {/* Origin */}
                    <div className="relative">
                      <div className="absolute -left-6 top-0.5 w-4 h-4 rounded-full bg-forest border-2 border-white shadow-sm" />
                      <div className="text-xs">
                        <span className="font-semibold text-ink-primary block">Pickup Confirmed (11:20 AM)</span>
                        <span className="text-ink-secondary text-[11px]">Volunteer: Aarav Sharma (Two-Wheeler Insulated Box)</span>
                      </div>
                    </div>
                    {/* Destination */}
                    <div className="relative">
                      <div className="absolute -left-6 top-0.5 w-4 h-4 rounded-full bg-terracotta border-2 border-white shadow-sm" />
                      <div className="text-xs">
                        <span className="font-semibold text-ink-primary block">Target Destination</span>
                        <span className="text-ink-secondary text-[11px]">Robin Hood Army • Indiranagar Slum Welfare Camp</span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Verified Chain of Custody Footer */}
                <div className="pt-3 border-t border-border flex items-center justify-between text-[11px] text-ink-secondary">
                  <div className="flex items-center gap-1.5 text-forest">
                    <ShieldCheck className="w-4 h-4" />
                    <span className="font-medium">OTP Verified Handover</span>
                  </div>
                  <span className="text-ink-muted">Calculated CO2e Saved: 56.2 kg</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
