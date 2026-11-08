import React from 'react';
import { HeroSection } from '../components/home/HeroSection';
import { ProblemStatement } from '../components/home/ProblemStatement';
import { HowItWorks } from '../components/home/HowItWorks';
import { RoleCards } from '../components/home/RoleCards';
import { ImpactSection } from '../components/home/ImpactSection';
import { IndianShowcase } from '../components/home/IndianShowcase';
import { CallToAction } from '../components/home/CallToAction';

export const Home: React.FC = () => {
  return (
    <main className="space-y-0">
      <HeroSection />
      <ProblemStatement />
      <HowItWorks />
      <RoleCards />
      <ImpactSection />
      <IndianShowcase />
      <CallToAction />
    </main>
  );
};

