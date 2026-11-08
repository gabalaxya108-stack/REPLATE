import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/common/Button';
import { ArrowLeft, Utensils } from 'lucide-react';

export const NotFound: React.FC = () => {
  return (
    <div className="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
      <div className="w-16 h-16 rounded-2xl bg-forest-subtle flex items-center justify-center text-forest mb-6">
        <Utensils className="w-8 h-8" />
      </div>
      <span className="text-4xl font-extrabold text-ink-primary tracking-tight">404</span>
      <h1 className="text-xl font-bold text-ink-primary mt-2">Page Not Found</h1>
      <p className="text-sm text-ink-secondary mt-1 max-w-sm">
        The requested URL was not found on REPLATE. Let's return to the home page or browse the live food feed.
      </p>
      <div className="mt-6 flex items-center gap-3">
        <Link to="/">
          <Button variant="primary" size="sm" leftIcon={<ArrowLeft className="w-4 h-4" />}>
            Back to Home
          </Button>
        </Link>
        <Link to="/explore">
          <Button variant="outline" size="sm">
            Explore Food Feed
          </Button>
        </Link>
      </div>
    </div>
  );
};

