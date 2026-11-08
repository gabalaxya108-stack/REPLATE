import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../common/Card';
import { Button } from '../common/Button';
import { ChefHat, Building2, Bike, ShieldCheck, ArrowRight } from 'lucide-react';

export const RoleCards: React.FC = () => {
  const roles = [
    {
      roleKey: 'DONOR',
      title: 'Food Donors',
      subtitle: 'Restaurants, Banquets, Hostels, Caterers',
      icon: <ChefHat className="w-6 h-6 text-forest" />,
      features: [
        'Post surplus within 60 seconds with expiry tracking',
        'Approve or decline NGO requests in real time',
        'Receive FSSAI-compliant digital handover records',
        'Download monthly ESG & CSR environmental impact reports',
      ],
      sample: 'e.g. Saravana Bhavan, Haldiram’s, Trident Hotels',
      actionText: 'Register as Donor',
      link: '/register?role=DONOR',
    },
    {
      roleKey: 'NGO',
      title: 'NGOs & Welfare Homes',
      subtitle: 'Verified Welfare & Community Kitchens',
      icon: <Building2 className="w-6 h-6 text-terracotta" />,
      features: [
        'Real-time geo-filtered surplus meal discovery',
        'Request exact portions matching current capacity',
        'Monitor incoming courier transit with live ETA',
        'Digitally verify batches using NGO confirmation code',
      ],
      sample: 'e.g. Robin Hood Army, Roti Bank Mumbai, Akshaya Patra',
      actionText: 'Apply for NGO Access',
      link: '/register?role=NGO',
    },
    {
      roleKey: 'VOLUNTEER',
      title: 'Volunteer Couriers',
      subtitle: 'Civic Champions & Student Corps',
      icon: <Bike className="w-6 h-6 text-forest-dark" />,
      features: [
        'Claim flexible delivery runs in your neighborhood',
        'Inspect packaging & thermal standards at pickup',
        'Seamless dual-OTP pickup and handover verification',
        'Earn platform appreciation badges & certificates',
      ],
      sample: 'Two-wheeler, four-wheeler or walking couriers',
      actionText: 'Join Volunteer Network',
      link: '/register?role=VOLUNTEER',
    },
    {
      roleKey: 'ADMIN',
      title: 'Platform Governance',
      subtitle: 'Audit, Compliance & Operations',
      icon: <ShieldCheck className="w-6 h-6 text-ink-primary" />,
      features: [
        'Audit 80G and NITI Aayog Darpan credentials',
        'Real-time allocation monitoring and safety alerts',
        'Comprehensive PostgreSQL transactional audit trails',
        'Regional heatmap of food diversion vs. demand',
      ],
      sample: 'Food safety auditors & system administrators',
      actionText: 'Admin Portal Login',
      link: '/login?role=ADMIN',
    },
  ];

  return (
    <section className="py-24 bg-surface border-t border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <span className="text-xs font-bold uppercase tracking-widest text-forest">
            Inclusive Ecosystem
          </span>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
            Tailored interfaces for every stakeholder.
          </h2>
          <p className="text-base text-ink-secondary">
            Each role operates within a dedicated workflow with role-based permissions, strict validation, and intuitive dashboards.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mt-16">
          {roles.map((item, idx) => (
            <Card key={idx} padding="md" hoverEffect className="flex flex-col justify-between space-y-6 bg-warm-card">
              <div className="space-y-4">
                <div className="w-12 h-12 rounded-xl bg-forest-subtle flex items-center justify-center">
                  {item.icon}
                </div>
                <div>
                  <h3 className="font-bold text-ink-primary text-xl">{item.title}</h3>
                  <p className="text-xs text-ink-secondary mt-0.5">{item.subtitle}</p>
                </div>
                <div className="text-[11px] font-medium text-forest-dark bg-forest-subtle/80 px-2.5 py-1 rounded-md">
                  {item.sample}
                </div>
                <ul className="space-y-2 pt-2">
                  {item.features.map((feat, fidx) => (
                    <li key={fidx} className="text-xs text-ink-primary flex items-start gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-forest mt-1.5 shrink-0" />
                      <span>{feat}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="pt-4 border-t border-border">
                <Link to={item.link}>
                  <Button variant="outline" size="sm" className="w-full justify-center group" rightIcon={<ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-1 transition-transform" />}>
                    {item.actionText}
                  </Button>
                </Link>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

