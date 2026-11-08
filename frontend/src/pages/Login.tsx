import React, { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { UserRole } from '../types';
import { Lock, Mail, ArrowRight, Info } from 'lucide-react';

export const Login: React.FC = () => {
  const [searchParams] = useSearchParams();

  const initialRole = (searchParams.get('role')?.toUpperCase() as UserRole) || 'DONOR';
  const [selectedRole, setSelectedRole] = useState<UserRole>(initialRole);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [isLoading, setIsLoading] = useState(false);
  const [loginFeedback, setLoginFeedback] = useState<string | null>(null);

  // Quick Demo credentials for fast Academic Viva Evaluation
  const demoAccounts: Record<UserRole, { email: string; name: string; org: string }> = {
    DONOR: {
      email: 'contact@saravanabhavan.in',
      name: 'Saravana Bhavan Foods Pvt Ltd',
      org: 'Indiranagar, Bengaluru',
    },
    NGO: {
      email: 'foodrescue@robinhoodarmy.org',
      name: 'Robin Hood Army (Bengaluru)',
      org: 'Darpan ID: KA/2018/0192834',
    },
    VOLUNTEER: {
      email: 'aarav.sharma@gmail.com',
      name: 'Aarav Sharma',
      org: 'Courier • Two-Wheeler',
    },
    ADMIN: {
      email: 'admin@replate.org',
      name: 'Replate Platform Auditor',
      org: 'Operations & Food Safety Lead',
    },
  };

  const handleQuickFill = (role: UserRole) => {
    setSelectedRole(role);
    setEmail(demoAccounts[role].email);
    setPassword('Password@123');
    setErrors({});
    setLoginFeedback(null);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: { email?: string; password?: string } = {};

    if (!email) {
      newErrors.email = 'Email address is required';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Please provide a valid email format';
    }

    if (!password) {
      newErrors.password = 'Password is required';
    } else if (password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);
    setLoginFeedback(null);

    setTimeout(() => {
      setIsLoading(false);
      setLoginFeedback(
        `Authentication evaluated successfully for [${selectedRole}] role (${email}). Phase 1 UI active — Spring Boot 3 JWT endpoint integration proceeds in Phase 2.`
      );
    }, 700);
  };

  return (
    <div className="min-h-[calc(100vh-16rem)] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-6">
        {/* Header */}
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-xl bg-forest mx-auto flex items-center justify-center text-white shadow-sm">
            <span className="font-extrabold text-xl">R</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-ink-primary tracking-tight">
            Sign in to REPLATE
          </h2>
          <p className="text-xs text-ink-secondary">
            Select your account type to access your tailored dashboard
          </p>
        </div>

        {/* Role Selector Tabs */}
        <div className="grid grid-cols-4 gap-1.5 p-1 bg-warm-muted rounded-xl border border-border">
          {(['DONOR', 'NGO', 'VOLUNTEER', 'ADMIN'] as UserRole[]).map((role) => (
            <button
              key={role}
              type="button"
              onClick={() => handleQuickFill(role)}
              className={`py-2 text-[11px] font-bold rounded-lg transition-all ${
                selectedRole === role
                  ? 'bg-surface text-forest-dark shadow-sm border border-border'
                  : 'text-ink-secondary hover:text-ink-primary'
              }`}
            >
              {role}
            </button>
          ))}
        </div>

        {/* Demo Fast-Switch Quick Bar */}
        <div className="bg-forest-subtle/80 border border-forest/20 rounded-xl p-3.5 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-bold uppercase tracking-wider text-forest-dark flex items-center gap-1.5">
              <Info className="w-3.5 h-3.5 text-forest" />
              Academic Viva Quick-Select
            </span>
            <span className="text-[10px] text-ink-secondary">Demo Credentials</span>
          </div>
          <p className="text-xs text-ink-primary">
            Testing as <strong>{demoAccounts[selectedRole].name}</strong> ({demoAccounts[selectedRole].org})
          </p>
          <button
            type="button"
            onClick={() => handleQuickFill(selectedRole)}
            className="text-[11px] font-semibold text-forest hover:text-forest-dark underline underline-offset-2"
          >
            Auto-fill credentials for {selectedRole}
          </button>
        </div>

        {/* Form Card */}
        <Card padding="lg" className="space-y-5 bg-surface">
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email Address"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="e.g. contact@saravanabhavan.in"
              error={errors.email}
              leftIcon={<Mail className="w-4 h-4" />}
            />

            <Input
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              error={errors.password}
              leftIcon={<Lock className="w-4 h-4" />}
            />

            <div className="flex items-center justify-between text-xs">
              <label className="flex items-center gap-2 text-ink-secondary cursor-pointer">
                <input type="checkbox" className="rounded border-border text-forest focus:ring-forest" defaultChecked />
                <span>Remember me</span>
              </label>
              <a href="#" className="font-medium text-forest hover:underline">
                Forgot password?
              </a>
            </div>

            <Button
              type="submit"
              variant="primary"
              size="md"
              className="w-full justify-center"
              isLoading={isLoading}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              Sign In as {selectedRole}
            </Button>
          </form>

          {/* Feedback message */}
          {loginFeedback && (
            <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-lg text-xs text-emerald-800 space-y-1">
              <div className="font-bold flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-600" />
                <span>Verification State Initialized</span>
              </div>
              <p className="text-[11px] text-emerald-700 leading-snug">{loginFeedback}</p>
            </div>
          )}

          {/* Footer inside card */}
          <div className="pt-4 border-t border-border text-center text-xs text-ink-secondary">
            Don't have an account yet?{' '}
            <Link to={`/register?role=${selectedRole}`} className="font-bold text-forest hover:underline">
              Create an account
            </Link>
          </div>
        </Card>
      </div>
    </div>
  );
};
