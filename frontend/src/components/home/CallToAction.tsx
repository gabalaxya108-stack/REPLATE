import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../common/Button';
import { ArrowRight, ShieldCheck } from 'lucide-react';

export const CallToAction: React.FC = () => {
  return (
    <section className="py-20 bg-forest text-white relative overflow-hidden">
      {/* Decorative background circle */}
      <div className="absolute -right-20 -bottom-20 w-96 h-96 bg-forest-light/30 rounded-full blur-2xl pointer-events-none" />
      <div className="absolute -left-20 -top-20 w-80 h-80 bg-sage/10 rounded-full blur-2xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="max-w-3xl mx-auto text-center space-y-8">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-forest-dark/60 border border-white/20 text-xs font-semibold tracking-wide text-sage-light">
            <ShieldCheck className="w-4 h-4 text-emerald-400" />
            <span>Zero Landfill Food Policy</span>
          </div>

          <h2 className="text-3xl sm:text-5xl font-extrabold tracking-tight text-white leading-tight">
            Ready to ensure no edible meal is wasted in your city?
          </h2>

          <p className="text-base sm:text-lg text-sage-light leading-relaxed font-normal">
            Join hundreds of Indian commercial kitchens, grassroots relief foundations, and volunteers actively redistributing surplus food daily.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            <Link to="/register?role=DONOR">
              <Button variant="terracotta" size="lg" rightIcon={<ArrowRight className="w-4 h-4" />}>
                Register Your Kitchen
              </Button>
            </Link>
            <Link to="/register?role=NGO">
              <Button variant="outline" size="lg" className="bg-transparent text-white border-white/30 hover:bg-white/10 hover:text-white">
                Register as NGO Partner
              </Button>
            </Link>
          </div>

          <div className="pt-6 flex flex-wrap items-center justify-center gap-8 text-xs text-sage-light/80">
            <span className="flex items-center gap-2">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              Free for non-profit welfare homes
            </span>
            <span className="flex items-center gap-2">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              FSSAI compliant transfer receipt
            </span>
            <span className="flex items-center gap-2">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              Dual-OTP verified delivery
            </span>
          </div>
        </div>
      </div>
    </section>
  );
};
