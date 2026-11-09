import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DonationResponse } from '../types/donation';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Input } from '../components/common/Input';
import { 
  Search, MapPin, Clock, Info, Utensils, RefreshCw, 
  AlertCircle, ShieldCheck, CheckCircle2, Building2, ArrowRight
} from 'lucide-react';

export const ExploreDonations: React.FC = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [donations, setDonations] = useState<DonationResponse[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedDiet, setSelectedDiet] = useState<string>('ALL');
  const [selectedCity, setSelectedCity] = useState<string>('ALL');
  const [currentPage, setCurrentPage] = useState(0);

  const [selectedDonation, setSelectedDonation] = useState<DonationResponse | null>(null);

  // Request Modal State
  const [requestModalDonation, setRequestModalDonation] = useState<DonationResponse | null>(null);
  const [requestedQty, setRequestedQty] = useState<string>('');
  const [intendedBeneficiaries, setIntendedBeneficiaries] = useState<string>('');
  const [isSubmittingRequest, setIsSubmittingRequest] = useState<boolean>(false);
  const [requestModalError, setRequestModalError] = useState<string | null>(null);
  const [requestSuccessNotice, setRequestSuccessNotice] = useState<string | null>(null);

  const categories = [
    { key: 'ALL', label: 'All Categories' },
    { key: 'COOKED_MEALS', label: 'Cooked Meals' },
    { key: 'BREADS_ROTIS', label: 'Breads & Rotis' },
    { key: 'DAIRY_SWEETS', label: 'Dairy & Sweets' },
    { key: 'RAW_PRODUCE', label: 'Raw Produce' },
    { key: 'PACKAGED_GOODS', label: 'Packaged Dry Goods' },
  ];

  const cities = [
    { key: 'ALL', label: 'All Operational Cities' },
    { key: 'Bengaluru', label: 'Bengaluru' },
    { key: 'New Delhi', label: 'New Delhi (NCR)' },
    { key: 'Mumbai', label: 'Mumbai' },
    { key: 'Hyderabad', label: 'Hyderabad' },
    { key: 'Pune', label: 'Pune' },
    { key: 'Chennai', label: 'Chennai' },
  ];

  const fetchPublicDonations = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.searchPublicDonations({
        category: selectedCategory,
        dietaryType: selectedDiet,
        city: selectedCity,
        query: searchQuery,
        page: currentPage,
        size: 12,
      });
      setDonations(response.content);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Unable to load surplus food batches from server.');
    } finally {
      setIsLoading(false);
    }
  }, [selectedCategory, selectedDiet, selectedCity, searchQuery, currentPage]);

  useEffect(() => {
    fetchPublicDonations();
  }, [fetchPublicDonations]);

  const calculateHoursLeft = (expiryTimeIso: string) => {
    const diffMs = new Date(expiryTimeIso).getTime() - Date.now();
    if (diffMs <= 0) return 'Expired';
    const hours = Math.floor(diffMs / 3600000);
    const mins = Math.floor((diffMs % 3600000) / 60000);
    if (hours > 0) return `${hours}h ${mins}m remaining`;
    return `${mins}m remaining`;
  };

  const handleOpenRequestModal = (donation: DonationResponse) => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (user?.role !== 'NGO' && user?.role !== 'ADMIN') {
      alert('Only registered NGOs can submit food redistribution claims. Donors and volunteers can explore listings or view their respective portals.');
      return;
    }
    setRequestModalDonation(donation);
    setRequestedQty(donation.availableQuantity.toString());
    setIntendedBeneficiaries('');
    setRequestModalError(null);
  };

  const handleSubmitRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!requestModalDonation) return;

    const qty = parseFloat(requestedQty);
    if (isNaN(qty) || qty <= 0) {
      setRequestModalError('Please enter a valid requested quantity greater than zero.');
      return;
    }
    if (qty > requestModalDonation.availableQuantity) {
      setRequestModalError(`Requested quantity cannot exceed available quantity (${requestModalDonation.availableQuantity} ${requestModalDonation.quantityUnit}).`);
      return;
    }
    if (!intendedBeneficiaries.trim() || intendedBeneficiaries.trim().length < 5) {
      setRequestModalError('Please specify the intended beneficiaries (minimum 5 characters).');
      return;
    }

    setIsSubmittingRequest(true);
    setRequestModalError(null);
    try {
      await api.createFoodRequest({
        donationId: requestModalDonation.id,
        requestedQuantity: qty,
        intendedBeneficiaries: intendedBeneficiaries.trim(),
      });
      setRequestModalDonation(null);
      setRequestSuccessNotice(`Claim for "${requestModalDonation.title}" submitted successfully! Awaiting donor review.`);
      setTimeout(() => setRequestSuccessNotice(null), 6000);
    } catch (err: any) {
      setRequestModalError(err instanceof ApiError ? err.message : 'Failed to submit food redistribution claim.');
    } finally {
      setIsSubmittingRequest(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      {/* Header */}
      <div className="max-w-3xl space-y-3 mb-10">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-forest-subtle text-forest text-xs font-semibold uppercase tracking-wider">
          <Utensils className="w-3.5 h-3.5" />
          <span>PostgreSQL Live Surplus Listings</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-ink-primary tracking-tight">
          Surplus Food Available for Redistribution
        </h1>
        <p className="text-sm text-ink-secondary leading-relaxed">
          Active batches posted by restaurants, hotels and caterers across Bengaluru, Delhi NCR, Mumbai, and Hyderabad. Verified NGOs can view specifications and request allocations.
        </p>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-surface border border-border rounded-xl p-4 shadow-card mb-8 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-3 items-center">
          <div className="md:col-span-5">
            <Input
              placeholder="Search by meal, kitchen name, or dish..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(0);
              }}
              leftIcon={<Search className="w-4 h-4" />}
            />
          </div>
          <div className="md:col-span-2">
            <select
              value={selectedCategory}
              onChange={(e) => {
                setSelectedCategory(e.target.value);
                setCurrentPage(0);
              }}
              className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
            >
              {categories.map((c) => (
                <option key={c.key} value={c.key}>
                  {c.label}
                </option>
              ))}
            </select>
          </div>
          <div className="md:col-span-2">
            <select
              value={selectedDiet}
              onChange={(e) => {
                setSelectedDiet(e.target.value);
                setCurrentPage(0);
              }}
              className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
            >
              <option value="ALL">All Dietary</option>
              <option value="PURE_VEG">Pure Veg Only</option>
              <option value="NON_VEG">Non-Veg</option>
              <option value="VEGAN">Vegan</option>
            </select>
          </div>
          <div className="md:col-span-3">
            <select
              value={selectedCity}
              onChange={(e) => {
                setSelectedCity(e.target.value);
                setCurrentPage(0);
              }}
              className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
            >
              {cities.map((c) => (
                <option key={c.key} value={c.key}>
                  {c.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Quick pill stats */}
        <div className="flex items-center justify-between text-xs text-ink-secondary pt-2 border-t border-border">
          <span>
            Showing <strong className="text-ink-primary">{donations.length}</strong> of{' '}
            <strong className="text-ink-primary">{totalElements}</strong> active surplus batches
          </span>
          <div className="flex items-center gap-3">
            <span className="text-emerald-700 font-medium flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              Live PostgreSQL Feed
            </span>
            <button
              onClick={fetchPublicDonations}
              className="text-forest hover:text-forest-dark flex items-center gap-1 font-semibold"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} />
              Sync
            </button>
          </div>
        </div>
      </div>

      {/* Success Banner */}
      {requestSuccessNotice && (
        <div className="mb-8 p-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 flex items-center justify-between shadow-sm animate-in fade-in duration-200">
          <div className="flex items-center gap-2.5">
            <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
            <div>
              <span className="font-bold block">Redistribution Claim Submitted</span>
              <span>{requestSuccessNotice}</span>
            </div>
          </div>
          <Link
            to="/ngo/requests"
            className="font-bold text-forest hover:text-forest-dark inline-flex items-center gap-1 shrink-0 ml-4 underline"
          >
            Track in NGO Console
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      )}

      {/* Error state */}
      {error && (
        <div className="mb-8 p-4 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2.5">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 shrink-0" />
          <div>
            <span className="font-bold block">Feed Notification</span>
            <span>{error}</span>
          </div>
        </div>
      )}

      {/* Loading state */}
      {isLoading ? (
        <div className="py-24 text-center">
          <RefreshCw className="w-8 h-8 text-forest animate-spin mx-auto mb-3" />
          <p className="text-sm font-medium text-ink-secondary">Querying active food donations from PostgreSQL...</p>
        </div>
      ) : donations.length === 0 ? (
        <div className="text-center py-16 bg-surface rounded-xl border border-border">
          <Utensils className="w-12 h-12 text-ink-muted mx-auto mb-3 opacity-60" />
          <h3 className="text-base font-bold text-ink-primary">No Surplus Food Batches Found</h3>
          <p className="text-xs text-ink-secondary mt-1">Try resetting the search keywords or filters to see all available donations.</p>
        </div>
      ) : (
        /* Donation Grid */
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {donations.map((donation) => {
            const percentRemaining = donation.totalQuantity > 0
              ? Math.round((donation.availableQuantity / donation.totalQuantity) * 100)
              : 0;

            return (
              <Card
                key={donation.id}
                padding="md"
                hoverEffect
                className="flex flex-col justify-between space-y-4 bg-surface"
              >
                <div className="space-y-3">
                  {/* Header: Diet and Status */}
                  <div className="flex items-center justify-between">
                    <Badge variant={donation.dietaryType === 'PURE_VEG' ? 'veg' : 'non-veg'}>
                      {donation.dietaryType === 'PURE_VEG' ? 'Pure Veg' : donation.dietaryType === 'VEGAN' ? 'Vegan' : 'Non-Veg'}
                    </Badge>
                    <Badge variant={donation.status === 'AVAILABLE' ? 'available' : 'allocated'}>
                      {donation.status === 'AVAILABLE' ? 'Available to Claim' : 'Partial Reserved'}
                    </Badge>
                  </div>

                  {/* Title & Donor */}
                  <div>
                    <h3 className="font-bold text-ink-primary text-base leading-snug line-clamp-1">
                      {donation.title}
                    </h3>
                    <div className="flex items-center gap-1.5 text-xs text-ink-secondary mt-1">
                      <MapPin className="w-3.5 h-3.5 text-forest shrink-0" />
                      <span className="truncate">{donation.donorName} • {donation.donorCity}</span>
                    </div>
                  </div>

                  {/* Thermal & Quantity Specs */}
                  <div className="bg-warm-muted/60 p-3 rounded-lg space-y-2 text-xs">
                    <div className="flex justify-between items-center">
                      <span className="text-ink-secondary">Available Quantity:</span>
                      <span className="font-bold text-forest">
                        {donation.availableQuantity} of {donation.totalQuantity} {donation.quantityUnit}
                      </span>
                    </div>

                    {/* Stock Progress Bar */}
                    <div className="w-full bg-border rounded-full h-1.5 overflow-hidden">
                      <div
                        className="bg-forest h-1.5 rounded-full transition-all duration-300"
                        style={{ width: `${percentRemaining}%` }}
                      />
                    </div>

                    <div className="flex justify-between items-center text-[11px] pt-1">
                      <span className="text-ink-secondary flex items-center gap-1">
                        <Clock className="w-3 h-3 text-terracotta" />
                        {calculateHoursLeft(donation.expiryTime)}
                      </span>
                      <span className="text-forest font-medium">{donation.storageCondition.replace('_', ' ')}</span>
                    </div>
                  </div>
                </div>

                {/* Card Footer Actions */}
                <div className="pt-3 border-t border-border flex items-center justify-between">
                  <button
                    onClick={() => setSelectedDonation(donation)}
                    className="text-xs font-semibold text-forest hover:text-forest-dark transition-colors inline-flex items-center gap-1"
                  >
                    <Info className="w-3.5 h-3.5" />
                    View Details
                  </button>
                  <Button
                    size="sm"
                    variant="primary"
                    onClick={() => handleOpenRequestModal(donation)}
                  >
                    Request Batch
                  </Button>
                </div>
              </Card>
            );
          })}
        </div>
      )}

      {/* Detail Modal */}
      {selectedDonation && (
        <div className="fixed inset-0 z-50 bg-ink-primary/40 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-150">
          <div className="bg-surface rounded-2xl border border-border max-w-lg w-full p-6 space-y-5 shadow-2xl">
            <div className="flex items-start justify-between">
              <div>
                <Badge variant={selectedDonation.dietaryType === 'PURE_VEG' ? 'veg' : 'non-veg'} className="mb-2">
                  {selectedDonation.dietaryType === 'PURE_VEG' ? 'Pure Veg' : 'Non-Veg'}
                </Badge>
                <h3 className="text-xl font-bold text-ink-primary">{selectedDonation.title}</h3>
                <p className="text-xs text-ink-secondary mt-1 flex items-center gap-1.5">
                  <MapPin className="w-3.5 h-3.5 text-forest" />
                  {selectedDonation.donorName} • {selectedDonation.donorCity}
                </p>
              </div>
              <button
                onClick={() => setSelectedDonation(null)}
                className="text-ink-secondary hover:text-ink-primary p-1 rounded-lg"
              >
                ✕
              </button>
            </div>

            <div className="space-y-2.5 text-xs bg-warm-muted/70 p-4 rounded-xl">
              <div className="flex justify-between">
                <span className="text-ink-secondary">Category:</span>
                <span className="font-semibold text-ink-primary">{selectedDonation.category.replace('_', ' ')}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-ink-secondary">Available Stock:</span>
                <span className="font-bold text-forest">
                  {selectedDonation.availableQuantity} of {selectedDonation.totalQuantity} {selectedDonation.quantityUnit}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-ink-secondary">Preparation Time:</span>
                <span className="font-semibold text-ink-primary">{new Date(selectedDonation.preparationTime).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-ink-secondary">Safe Expiry Window:</span>
                <span className="font-semibold text-terracotta">{new Date(selectedDonation.expiryTime).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-ink-secondary">Thermal State:</span>
                <span className="font-semibold text-ink-primary">{selectedDonation.storageCondition.replace('_', ' ')}</span>
              </div>
            </div>

            {selectedDonation.pickupNotes && (
              <div className="space-y-1.5">
                <span className="text-xs font-bold text-ink-primary uppercase tracking-wider block">Pickup & Dispatch Notes</span>
                <p className="text-xs text-ink-secondary leading-relaxed bg-warm-card p-3 rounded-lg border border-border">
                  {selectedDonation.pickupNotes}
                </p>
              </div>
            )}

            <div className="flex items-center gap-2 p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800">
              <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0" />
              <span>Donor verified under FSSAI food donation safety regulations.</span>
            </div>

            <div className="pt-2 flex items-center justify-end gap-3 border-t border-border">
              <Button variant="outline" size="sm" onClick={() => setSelectedDonation(null)}>
                Close
              </Button>
              <Button
                variant="primary"
                size="sm"
                onClick={() => {
                  const target = selectedDonation;
                  setSelectedDonation(null);
                  handleOpenRequestModal(target);
                }}
              >
                Proceed to Request
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* NGO Request Batch Modal */}
      {requestModalDonation && (
        <div className="fixed inset-0 z-50 bg-ink-primary/50 backdrop-blur-xs flex items-center justify-center p-4 animate-in fade-in duration-150">
          <div className="bg-surface rounded-2xl border border-border max-w-lg w-full p-6 space-y-5 shadow-2xl">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-blue-100 text-blue-800 border border-blue-300">
                  Redistribution Claim Application
                </span>
                <h3 className="text-xl font-bold text-ink-primary mt-1.5">
                  Request: {requestModalDonation.title}
                </h3>
                <p className="text-xs text-ink-secondary flex items-center gap-1.5 mt-0.5">
                  <Building2 className="w-3.5 h-3.5 text-forest" />
                  Offered by {requestModalDonation.donorName} ({requestModalDonation.donorCity})
                </p>
              </div>
              <button
                onClick={() => setRequestModalDonation(null)}
                disabled={isSubmittingRequest}
                className="text-ink-secondary hover:text-ink-primary p-1 rounded-lg"
              >
                ✕
              </button>
            </div>

            {requestModalError && (
              <div className="p-3.5 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2">
                <AlertCircle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
                <span>{requestModalError}</span>
              </div>
            )}

            <form onSubmit={handleSubmitRequest} className="space-y-4">
              <div className="bg-warm-muted/70 p-3.5 rounded-xl space-y-1.5 text-xs">
                <div className="flex justify-between">
                  <span className="text-ink-secondary">Available in Batch:</span>
                  <span className="font-bold text-forest">
                    {requestModalDonation.availableQuantity} {requestModalDonation.quantityUnit}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-ink-secondary">Category & Diet:</span>
                  <span className="font-semibold text-ink-primary">
                    {requestModalDonation.category.replace('_', ' ')} • {requestModalDonation.dietaryType.replace('_', ' ')}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-ink-secondary">Safe Redistribution Window:</span>
                  <span className="font-semibold text-terracotta">
                    {calculateHoursLeft(requestModalDonation.expiryTime)}
                  </span>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-ink-primary mb-1">
                  Requested Quantity ({requestModalDonation.quantityUnit}) *
                </label>
                <Input
                  type="number"
                  min="0.1"
                  max={requestModalDonation.availableQuantity}
                  step="any"
                  value={requestedQty}
                  onChange={(e) => setRequestedQty(e.target.value)}
                  placeholder={`Max ${requestModalDonation.availableQuantity}`}
                  required
                />
                <span className="text-[11px] text-ink-muted mt-1 block">
                  You can claim full or partial quantity up to {requestModalDonation.availableQuantity} {requestModalDonation.quantityUnit}.
                </span>
              </div>

              <div>
                <label className="block text-xs font-semibold text-ink-primary mb-1">
                  Intended Beneficiary Purpose & Distribution Details *
                </label>
                <textarea
                  rows={3}
                  value={intendedBeneficiaries}
                  onChange={(e) => setIntendedBeneficiaries(e.target.value)}
                  placeholder="e.g., Distribution to 45 resident children and senior citizens at Hope Community Shelter, Indiranagar."
                  className="w-full rounded-lg border border-border bg-surface p-3 text-xs text-ink-primary placeholder:text-ink-muted focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  required
                />
                <span className="text-[11px] text-ink-muted mt-0.5 block">
                  The commercial kitchen donor will review this justification before confirming dispatch.
                </span>
              </div>

              <div className="pt-3 border-t border-border flex items-center justify-end gap-3">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setRequestModalDonation(null)}
                  disabled={isSubmittingRequest}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  size="sm"
                  disabled={isSubmittingRequest}
                  leftIcon={isSubmittingRequest ? <RefreshCw className="w-4 h-4 animate-spin" /> : undefined}
                >
                  {isSubmittingRequest ? 'Submitting Claim...' : 'Submit Claim to Donor'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
