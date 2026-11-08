import React, { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { UserRole } from '../types';
import { ChefHat, Building2, Bike, ArrowRight, CheckCircle2 } from 'lucide-react';

export const Register: React.FC = () => {
  const [searchParams] = useSearchParams();
  const initialRole = (searchParams.get('role')?.toUpperCase() as UserRole) || 'DONOR';
  const [role, setRole] = useState<UserRole>(initialRole === 'ADMIN' ? 'DONOR' : initialRole);

  // Common Fields
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [city, setCity] = useState('Bengaluru');
  const [state, setState] = useState('Karnataka');
  const [pincode, setPincode] = useState('');

  // Role-Specific Fields
  // DONOR
  const [orgName, setOrgName] = useState('');
  const [orgType, setOrgType] = useState('RESTAURANT');
  const [fssaiLicense, setFssaiLicense] = useState('');

  // NGO
  const [ngoName, setNgoName] = useState('');
  const [darpanId, setDarpanId] = useState('');
  const [registration80g, setRegistration80g] = useState('');
  const [dailyCapacity, setDailyCapacity] = useState('100');

  // VOLUNTEER
  const [vehicleType, setVehicleType] = useState('TWO_WHEELER');
  const [emergencyContact, setEmergencyContact] = useState('');

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [successNotice, setSuccessNotice] = useState<string | null>(null);

  const handleRoleChange = (newRole: UserRole) => {
    setRole(newRole);
    setErrors({});
    setSuccessNotice(null);
  };

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: Record<string, string> = {};

    if (!fullName) newErrors.fullName = 'Full legal / entity name is required';
    if (!email || !/\S+@\S+\.\S+/.test(email)) newErrors.email = 'Valid email is required';
    if (!phoneNumber || phoneNumber.length < 10) newErrors.phoneNumber = 'Valid 10-digit phone number is required';
    if (!password || password.length < 6) newErrors.password = 'Password must be at least 6 characters';
    if (!pincode || pincode.length !== 6) newErrors.pincode = 'Valid 6-digit Indian pincode is required';

    if (role === 'DONOR') {
      if (!orgName) newErrors.orgName = 'Organization / kitchen name is required';
      if (!fssaiLicense || fssaiLicense.length < 10) newErrors.fssaiLicense = 'Valid FSSAI license number is required';
    } else if (role === 'NGO') {
      if (!ngoName) newErrors.ngoName = 'Registered NGO title is required';
      if (!darpanId) newErrors.darpanId = 'NITI Aayog Darpan ID is required for verification';
    } else if (role === 'VOLUNTEER') {
      if (!emergencyContact || emergencyContact.length < 10) newErrors.emergencyContact = 'Emergency phone contact is required';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setSuccessNotice(
        `Registration request recorded for [${role}] "${fullName}" in ${city}, ${state}. In Phase 2, this commits directly into PostgreSQL via Java Spring Boot authentication services!`
      );
    }, 800);
  };

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="text-center space-y-2 mb-8">
        <h1 className="text-3xl font-extrabold text-ink-primary tracking-tight">
          Join the REPLATE Food Recovery Network
        </h1>
        <p className="text-sm text-ink-secondary">
          Choose your stakeholder category to initialize your institutional profile
        </p>
      </div>

      {/* Role Selection Tabs */}
      <div className="grid grid-cols-3 gap-3 mb-8">
        {[
          { key: 'DONOR' as UserRole, label: 'Commercial Donor', sub: 'Restaurant / Banquet', icon: <ChefHat className="w-5 h-5" /> },
          { key: 'NGO' as UserRole, label: 'Registered NGO', sub: 'Welfare / Shelter', icon: <Building2 className="w-5 h-5" /> },
          { key: 'VOLUNTEER' as UserRole, label: 'Volunteer Courier', sub: 'Community Transport', icon: <Bike className="w-5 h-5" /> },
        ].map((item) => (
          <button
            key={item.key}
            type="button"
            onClick={() => handleRoleChange(item.key)}
            className={`p-4 rounded-xl border text-left transition-all ${
              role === item.key
                ? 'border-forest bg-forest-subtle shadow-sm ring-1 ring-forest'
                : 'border-border bg-surface hover:border-sage'
            }`}
          >
            <div className={`w-9 h-9 rounded-lg flex items-center justify-center mb-2 ${
              role === item.key ? 'bg-forest text-white' : 'bg-warm-muted text-ink-secondary'
            }`}>
              {item.icon}
            </div>
            <div className="font-bold text-sm text-ink-primary">{item.label}</div>
            <div className="text-[11px] text-ink-secondary mt-0.5">{item.sub}</div>
          </button>
        ))}
      </div>

      {/* Main Registration Form */}
      <Card padding="lg" className="bg-surface shadow-card">
        <form onSubmit={handleRegister} className="space-y-6">
          {/* Section: Common Identity */}
          <div className="space-y-4">
            <h3 className="text-xs font-bold uppercase tracking-wider text-forest border-b border-border pb-2">
              1. Basic Account & Identity
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Full Name / Representative"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="e.g. Chef Ramesh / Pooja Deshmukh"
                error={errors.fullName}
              />
              <Input
                label="Official Email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="contact@institution.in"
                error={errors.email}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Contact Number (+91)"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="+91-9876543210"
                error={errors.phoneNumber}
              />
              <Input
                label="Account Password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                error={errors.password}
              />
            </div>
          </div>

          {/* Section: Role Specific Verification */}
          <div className="space-y-4">
            <h3 className="text-xs font-bold uppercase tracking-wider text-forest border-b border-border pb-2">
              2. {role} Institutional Verification & Specifications
            </h3>

            {role === 'DONOR' && (
              <div className="space-y-4 animate-in fade-in duration-200">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Input
                    label="Establishment Name"
                    value={orgName}
                    onChange={(e) => setOrgName(e.target.value)}
                    placeholder="e.g. Saravana Bhavan Banquets"
                    error={errors.orgName}
                  />
                  <div className="space-y-1.5">
                    <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                      Establishment Type
                    </label>
                    <select
                      value={orgType}
                      onChange={(e) => setOrgType(e.target.value)}
                      className="w-full rounded-lg border border-border bg-surface py-2.5 px-3.5 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                    >
                      <option value="RESTAURANT">Restaurant / Dine-in</option>
                      <option value="HOTEL_BANQUET">Hotel Banquet Hall</option>
                      <option value="HOSTEL_MESS">Hostel / Campus Mess</option>
                      <option value="CATERER">Commercial Wedding Caterer</option>
                      <option value="SWEET_CORNER">Sweets & Dairy Shop</option>
                    </select>
                  </div>
                </div>

                <Input
                  label="14-Digit FSSAI License Number"
                  value={fssaiLicense}
                  onChange={(e) => setFssaiLicense(e.target.value)}
                  placeholder="11220334000123"
                  helperText="Mandatory under Food Safety and Standards Authority of India surplus distribution rules."
                  error={errors.fssaiLicense}
                />
              </div>
            )}

            {role === 'NGO' && (
              <div className="space-y-4 animate-in fade-in duration-200">
                <Input
                  label="NGO / Trust Legal Name"
                  value={ngoName}
                  onChange={(e) => setNgoName(e.target.value)}
                  placeholder="e.g. Robin Hood Army Welfare Foundation"
                  error={errors.ngoName}
                />

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Input
                    label="NITI Aayog Darpan ID"
                    value={darpanId}
                    onChange={(e) => setDarpanId(e.target.value)}
                    placeholder="KA/2018/0192834"
                    helperText="Enables immediate fast-track verified status."
                    error={errors.darpanId}
                  />
                  <Input
                    label="80G Certificate Number (Optional)"
                    value={registration80g}
                    onChange={(e) => setRegistration80g(e.target.value)}
                    placeholder="AAATR1234PF20214"
                  />
                </div>

                <Input
                  label="Daily Beneficiary Meal Capacity"
                  type="number"
                  value={dailyCapacity}
                  onChange={(e) => setDailyCapacity(e.target.value)}
                  placeholder="150"
                  helperText="Maximum hot meals your organization can safely distribute per day."
                />
              </div>
            )}

            {role === 'VOLUNTEER' && (
              <div className="space-y-4 animate-in fade-in duration-200">
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Primary Transit Vehicle
                  </label>
                  <select
                    value={vehicleType}
                    onChange={(e) => setVehicleType(e.target.value)}
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3.5 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  >
                    <option value="TWO_WHEELER">Two-Wheeler (Motorcycle / Scooter with bag)</option>
                    <option value="FOUR_WHEELER">Four-Wheeler (Car / Hatchback)</option>
                    <option value="AUTO_VAN">Commercial Auto / Cargo Van</option>
                    <option value="WALK">Bicycle / Walking (Neighborhood runs &lt; 1km)</option>
                  </select>
                </div>

                <Input
                  label="Emergency Contact Phone Number"
                  value={emergencyContact}
                  onChange={(e) => setEmergencyContact(e.target.value)}
                  placeholder="+91-9876500112"
                  error={errors.emergencyContact}
                />
              </div>
            )}
          </div>

          {/* Section: Location */}
          <div className="space-y-4">
            <h3 className="text-xs font-bold uppercase tracking-wider text-forest border-b border-border pb-2">
              3. Operational Location
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="space-y-1.5">
                <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                  City
                </label>
                <select
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                >
                  <option value="Bengaluru">Bengaluru</option>
                  <option value="New Delhi">New Delhi (NCR)</option>
                  <option value="Mumbai">Mumbai</option>
                  <option value="Hyderabad">Hyderabad</option>
                  <option value="Pune">Pune</option>
                  <option value="Chennai">Chennai</option>
                </select>
              </div>

              <div className="space-y-1.5">
                <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                  State
                </label>
                <input
                  type="text"
                  value={state}
                  onChange={(e) => setState(e.target.value)}
                  className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                />
              </div>

              <Input
                label="6-Digit Pincode"
                value={pincode}
                onChange={(e) => setPincode(e.target.value)}
                placeholder="560038"
                error={errors.pincode}
              />
            </div>
          </div>

          {/* Submit Button */}
          <div className="pt-4 border-t border-border">
            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full justify-center"
              isLoading={isLoading}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              Complete {role} Registration
            </Button>
          </div>
        </form>

        {successNotice && (
          <div className="mt-6 p-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 space-y-2">
            <div className="font-bold flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-700" />
              <span>Institutional Onboarding Initialized</span>
            </div>
            <p className="text-[12px] text-emerald-700 leading-relaxed">{successNotice}</p>
            <div className="pt-2">
              <Link to="/login" className="font-bold text-forest hover:underline">
                Proceed to Login Page →
              </Link>
            </div>
          </div>
        )}

        <div className="mt-6 pt-4 border-t border-border text-center text-xs text-ink-secondary">
          Already registered?{' '}
          <Link to="/login" className="font-bold text-forest hover:underline">
            Sign in to existing account
          </Link>
        </div>
      </Card>
    </div>
  );
};
