import React from 'react';
import { Card } from '../common/Card';
import { Utensils, Search, Bike, HeartHandshake, CheckCircle2 } from 'lucide-react';

export const HowItWorks: React.FC = () => {
  const steps = [
    {
      number: '01',
      icon: <Utensils className="w-6 h-6 text-forest" />,
      title: 'Donor Posts Surplus Batch',
      role: 'Commercial Kitchens & Banquets',
      description: 'The kitchen manager logs surplus food details: category, portions, cooking time, dietary type (Pure Veg / Non-Veg), and safe consumption expiry threshold.',
      highlights: ['Automatic FSSAI tagging', 'Expiry time countdown', 'Flexible quantity units'],
    },
    {
      number: '02',
      icon: <Search className="w-6 h-6 text-forest" />,
      title: 'Verified NGO Claims Batch',
      role: 'Shelters & Registered NGOs',
      description: 'Nearby verified NGOs discover the listing within minutes. They request exact meal allocations corresponding to their immediate community capacity.',
      highlights: ['Partial-quantity allocation', 'NITI Aayog Darpan audit', 'Instant donor approval'],
    },
    {
      number: '03',
      icon: <Bike className="w-6 h-6 text-forest" />,
      title: 'Volunteer Dispatch with Dual OTP',
      role: 'Community Couriers',
      description: 'A local volunteer accepts the delivery run. They verify food temperature and packaging at pickup using a 6-digit donor OTP before heading to the shelter.',
      highlights: ['Dual-OTP security', 'Temperature checks', 'Live GPS status tracker'],
    },
    {
      number: '04',
      icon: <HeartHandshake className="w-6 h-6 text-forest" />,
      title: 'Dignified Redistribution',
      role: 'Communities & Impact Audit',
      description: 'The NGO signs off upon handover. The system records the complete audit log in PostgreSQL and increments platform-wide carbon offset and meal counters.',
      highlights: ['Zero landfill dumping', 'CO2e offset certificate', 'Full digital audit trail'],
    },
  ];

  return (
    <section id="how-it-works" className="py-24 bg-warm-bg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <span className="text-xs font-bold uppercase tracking-widest text-forest">
            End-To-End Architecture
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
            How REPLATE facilitates rapid redistribution.
          </h2>
          <p className="text-base text-ink-secondary">
            A synchronized workflow linking commercial kitchens, grassroots organizations, and volunteers within an airtight chain of custody.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mt-16">
          {steps.map((step, idx) => (
            <div key={idx} className="relative flex flex-col">
              {/* Connector line for large screens */}
              {idx < steps.length - 1 && (
                <div className="hidden lg:block absolute top-8 left-full w-full h-0.5 bg-border -translate-x-4 z-0 pointer-events-none" />
              )}

              <Card className="flex-1 flex flex-col justify-between space-y-4 z-10 bg-surface">
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="w-12 h-12 rounded-xl bg-forest-subtle flex items-center justify-center">
                      {step.icon}
                    </div>
                    <span className="text-2xl font-black text-ink-muted/50 font-sans">
                      {step.number}
                    </span>
                  </div>

                  <div>
                    <span className="text-[11px] font-semibold text-terracotta-dark uppercase tracking-wider block">
                      {step.role}
                    </span>
                    <h3 className="font-bold text-ink-primary text-lg mt-1">
                      {step.title}
                    </h3>
                  </div>

                  <p className="text-xs text-ink-secondary leading-relaxed">
                    {step.description}
                  </p>
                </div>

                <div className="pt-4 border-t border-border/80 space-y-1.5">
                  {step.highlights.map((h, i) => (
                    <div key={i} className="flex items-center gap-2 text-[11px] text-ink-primary">
                      <CheckCircle2 className="w-3.5 h-3.5 text-forest shrink-0" />
                      <span>{h}</span>
                    </div>
                  ))}
                </div>
              </Card>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
