import React from 'react';
import { Card } from '../common/Card';
import { Badge } from '../common/Badge';
import { MapPin, ShieldCheck } from 'lucide-react';

export const IndianShowcase: React.FC = () => {
  const partners = [
    {
      name: "Saravana Bhavan Foods Pvt Ltd",
      category: "Restaurant Chain",
      city: "Bengaluru, Karnataka",
      fssai: "11220334000123",
      stats: "2,400+ meals redirected",
      specialty: "South Indian Breakfast & Rice Meals",
    },
    {
      name: "Haldiram's Sweets & Catering",
      category: "Catering & Sweets",
      city: "New Delhi, Delhi NCR",
      fssai: "13318005000456",
      stats: "4,150+ meals redirected",
      specialty: "North Indian Curries, Dal Makhani & Desserts",
    },
    {
      name: "Trident Nariman Point Banquets",
      category: "Luxury Banquets",
      city: "Mumbai, Maharashtra",
      fssai: "11516001000789",
      stats: "1,800+ banquet thalis",
      specialty: "Corporate Buffets & High-End Dinners",
    },
    {
      name: "Robin Hood Army (Bengaluru Chapter)",
      category: "Volunteer Welfare Network",
      city: "Bengaluru, Karnataka",
      darpan: "KA/2018/0192834",
      stats: "350 daily beneficiaries",
      specialty: "Slum Schools & Daily Wage Settlements",
    },
    {
      name: "Roti Bank Mumbai Foundation",
      category: "Mobile Food Recovery NGO",
      city: "Mumbai, Maharashtra",
      darpan: "MH/2017/0088712",
      stats: "800 daily beneficiaries",
      specialty: "Dadar, Chembur & Govandi Welfare Hubs",
    },
    {
      name: "Akshaya Patra Foundation",
      category: "Institutional Kitchen",
      city: "Bengaluru / Hubballi",
      darpan: "KA/2014/0010482",
      stats: "Hub logistics partner",
      specialty: "Large-scale insulated thermal transport",
    },
  ];

  return (
    <section id="network" className="py-24 bg-surface border-t border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <span className="text-xs font-bold uppercase tracking-widest text-forest">
            Pan-India Ecosystem
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
            Trusted by iconic kitchens and verified welfare leaders.
          </h2>
          <p className="text-base text-ink-secondary">
            From historic food establishments to grassroots relief teams, REPLATE powers trusted community distribution across Indian cities.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-16">
          {partners.map((partner, idx) => (
            <Card key={idx} padding="md" hoverEffect className="space-y-4 bg-warm-card">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="font-bold text-ink-primary text-base">
                    {partner.name}
                  </h3>
                  <div className="flex items-center gap-1.5 text-xs text-ink-secondary mt-1">
                    <MapPin className="w-3.5 h-3.5 text-forest" />
                    <span>{partner.city}</span>
                  </div>
                </div>
                <Badge variant="category">{partner.category}</Badge>
              </div>

              <div className="text-xs bg-warm-muted/70 p-3 rounded-lg space-y-1">
                <div className="flex items-center justify-between">
                  <span className="text-ink-secondary">Verification ID</span>
                  <span className="font-mono font-medium text-ink-primary text-[11px]">
                    {partner.fssai ? `FSSAI: ${partner.fssai}` : `Darpan: ${partner.darpan}`}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-ink-secondary">Specialty</span>
                  <span className="font-medium text-ink-primary">{partner.specialty}</span>
                </div>
              </div>

              <div className="pt-2 border-t border-border flex items-center justify-between text-xs">
                <span className="font-semibold text-forest">{partner.stats}</span>
                <span className="text-emerald-700 flex items-center gap-1 text-[11px] font-medium">
                  <ShieldCheck className="w-3.5 h-3.5" />
                  Verified
                </span>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};
