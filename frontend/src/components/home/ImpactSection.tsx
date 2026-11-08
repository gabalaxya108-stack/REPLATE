import React from 'react';
import { Card } from '../common/Card';
import { Sparkles, Leaf, Utensils, IndianRupee, Award } from 'lucide-react';

export const ImpactSection: React.FC = () => {
  const metrics = [
    {
      label: 'Meals Safely Diverted',
      value: '184,200+',
      unit: 'Hot Thalis & Portions',
      icon: <Utensils className="w-5 h-5 text-forest" />,
      subtext: 'Reaching night shelters, child homes & worker settlements',
    },
    {
      label: 'Landfill Methane Diverted',
      value: '460.5',
      unit: 'Tonnes CO₂e Avoided',
      icon: <Leaf className="w-5 h-5 text-emerald-600" />,
      subtext: 'Calculated via standard UNEP factor (2.5 kg CO2e / kg waste)',
    },
    {
      label: 'Economic Value Preserved',
      value: '₹1.56 Cr',
      unit: 'Indian Rupees',
      icon: <IndianRupee className="w-5 h-5 text-terracotta" />,
      subtext: 'Nutritional capital saved from premature destruction',
    },
    {
      label: 'Verified Indian Kitchens',
      value: '120+',
      unit: 'Restaurants & Banquets',
      icon: <Award className="w-5 h-5 text-forest-dark" />,
      subtext: 'Active partners across 5 metropolitan clusters',
    },
  ];

  return (
    <section id="impact" className="py-24 bg-forest-subtle/50 border-t border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-forest text-white text-xs font-semibold uppercase tracking-wider">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Measurable Sustainability</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
            Real environmental and social impact, verified by code.
          </h2>
          <p className="text-base text-ink-secondary">
            Every transaction executed through REPLATE records exact weight, timestamps, and geolocation, feeding transparent ESG audits.
          </p>
        </div>

        {/* Metrics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-16">
          {metrics.map((m, idx) => (
            <Card key={idx} padding="md" className="space-y-4 bg-surface border-border">
              <div className="w-10 h-10 rounded-lg bg-warm-muted flex items-center justify-center">
                {m.icon}
              </div>
              <div>
                <span className="text-3xl font-black text-ink-primary tracking-tight font-sans block">
                  {m.value}
                </span>
                <span className="text-xs font-bold text-forest uppercase tracking-wider block mt-0.5">
                  {m.unit}
                </span>
              </div>
              <div className="pt-2 border-t border-border">
                <span className="text-xs font-medium text-ink-primary block">
                  {m.label}
                </span>
                <p className="text-[11px] text-ink-secondary mt-0.5 leading-snug">
                  {m.subtext}
                </p>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};
