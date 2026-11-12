import React from 'react';
import { Card } from '../common/Card';
import { AlertTriangle, Clock, Truck, ShieldAlert, TrendingDown } from 'lucide-react';

export const ProblemStatement: React.FC = () => {
  return (
    <section className="py-20 bg-surface border-y border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Heading */}
        <div className="max-w-3xl mx-auto text-center space-y-4">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-terracotta/10 text-terracotta-dark text-xs font-semibold uppercase tracking-wider">
            <AlertTriangle className="w-3.5 h-3.5" />
            <span>The Food Waste Paradox in India</span>
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
            Why perfectly edible food is discarded while communities go hungry.
          </h2>
          <p className="text-base text-ink-secondary leading-relaxed">
            In urban India, millions of nutritious meals are prepared every evening for weddings, corporate buffets, and restaurants. Yet within a 4-hour window, lack of structured logistics turns hot meals into landfill waste.
          </p>
        </div>

        {/* 4 Core Bottlenecks */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-16">
          {/* Card 1 */}
          <Card padding="md" hoverEffect className="space-y-3 bg-warm-card">
            <div className="w-10 h-10 rounded-lg bg-red-500/15 text-red-700 dark:text-red-300 flex items-center justify-center font-bold">
              <Clock className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-ink-primary text-base">Perishable Time Window</h3>
            <p className="text-xs text-ink-secondary leading-relaxed">
              Cooked food has a strict 4–6 hour safe consumption threshold under Indian ambient weather. Without instant alerts, surplus goes past safe recovery time.
            </p>
          </Card>

          {/* Card 2 */}
          <Card padding="md" hoverEffect className="space-y-3 bg-warm-card">
            <div className="w-10 h-10 rounded-lg bg-amber-500/15 text-amber-800 dark:text-amber-300 flex items-center justify-center font-bold">
              <Truck className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-ink-primary text-base">The Last-Mile Transport Gap</h3>
            <p className="text-xs text-ink-secondary leading-relaxed">
              Shelters and grassroots NGOs often lack dedicated transport vehicles to pick up 80 kg of hot biryani across town at 11:00 PM when banquet kitchens close.
            </p>
          </Card>

          {/* Card 3 */}
          <Card padding="md" hoverEffect className="space-y-3 bg-warm-card">
            <div className="w-10 h-10 rounded-lg bg-emerald-500/15 text-emerald-800 dark:text-emerald-300 flex items-center justify-center font-bold">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-ink-primary text-base">FSSAI Safety Liabilities</h3>
            <p className="text-xs text-ink-secondary leading-relaxed">
              Commercial kitchens fear food safety liabilities. Without verified NGO accreditation and digital timestamping, donating feels legally risky.
            </p>
          </Card>

          {/* Card 4 */}
          <Card padding="md" hoverEffect className="space-y-3 bg-warm-card">
            <div className="w-10 h-10 rounded-lg bg-blue-500/15 text-blue-800 dark:text-blue-300 flex items-center justify-center font-bold">
              <TrendingDown className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-ink-primary text-base">Methane Emissions in Landfills</h3>
            <p className="text-xs text-ink-secondary leading-relaxed">
              Decomposing food in open dumps like Ghazipur (Delhi) and Deonar (Mumbai) releases potent methane gas, escalating urban greenhouse gas crises.
            </p>
          </Card>
        </div>
      </div>
    </section>
  );
};

