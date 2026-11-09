import React, { useState, useEffect, useCallback } from 'react';
import { api, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DonationResponse, CreateDonationRequest, UpdateDonationRequest } from '../types/donation';
import { FoodCategory, QuantityUnit, DietaryType, StorageCondition, DonationStatus } from '../types';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Input } from '../components/common/Input';
import { 
  Plus, Edit3, Send, XCircle, Clock, Utensils, AlertCircle, 
  CheckCircle2, RefreshCw, Eye, MapPin, ChefHat, ShieldCheck
} from 'lucide-react';

export const DonorDashboard: React.FC = () => {
  const { user } = useAuth();
  const [donations, setDonations] = useState<DonationResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Filter state
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  // Modal states
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDonation, setEditingDonation] = useState<DonationResponse | null>(null);
  const [detailDonation, setDetailDonation] = useState<DonationResponse | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  // Form Fields
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState<FoodCategory>('COOKED_MEALS');
  const [totalQuantity, setTotalQuantity] = useState('50');
  const [quantityUnit, setQuantityUnit] = useState<QuantityUnit>('MEAL_BOXES');
  const [dietaryType, setDietaryType] = useState<DietaryType>('PURE_VEG');
  const [storageCondition, setStorageCondition] = useState<StorageCondition>('HOT_INSULATED');
  const [prepTimeLocal, setPrepTimeLocal] = useState('');
  const [expiryTimeLocal, setExpiryTimeLocal] = useState('');
  const [pickupNotes, setPickupNotes] = useState('');
  const [publishImmediately, setPublishImmediately] = useState(true);

  // Initialize datetime strings
  const initDateTimeDefaults = () => {
    const now = new Date();
    const prep = new Date(now.getTime() - 30 * 60000); // 30 mins ago
    const exp = new Date(now.getTime() + 6 * 3600000); // 6 hours ahead

    const toLocalISO = (d: Date) => {
      const pad = (n: number) => (n < 10 ? '0' + n : n);
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    };

    setPrepTimeLocal(toLocalISO(prep));
    setExpiryTimeLocal(toLocalISO(exp));
  };

  const fetchMyDonations = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await api.getMyDonations(0, 50);
      setDonations(res.content);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Failed to load your food listings.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMyDonations();
  }, [fetchMyDonations]);

  const openCreateModal = () => {
    setEditingDonation(null);
    setTitle('');
    setCategory('COOKED_MEALS');
    setTotalQuantity('50');
    setQuantityUnit('MEAL_BOXES');
    setDietaryType('PURE_VEG');
    setStorageCondition('HOT_INSULATED');
    setPickupNotes('');
    setPublishImmediately(true);
    initDateTimeDefaults();
    setFormError(null);
    setIsModalOpen(true);
  };

  const openEditModal = (item: DonationResponse) => {
    setEditingDonation(item);
    setTitle(item.title);
    setCategory(item.category);
    setTotalQuantity(item.totalQuantity.toString());
    setQuantityUnit(item.quantityUnit);
    setDietaryType(item.dietaryType);
    setStorageCondition(item.storageCondition);

    const toLocalISO = (isoString: string) => {
      const d = new Date(isoString);
      const pad = (n: number) => (n < 10 ? '0' + n : n);
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    };

    setPrepTimeLocal(toLocalISO(item.preparationTime));
    setExpiryTimeLocal(toLocalISO(item.expiryTime));
    setPickupNotes(item.pickupNotes || '');
    setPublishImmediately(false);
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      setFormError('Title is required');
      return;
    }
    const qtyNum = parseFloat(totalQuantity);
    if (isNaN(qtyNum) || qtyNum <= 0) {
      setFormError('Total quantity must be greater than zero');
      return;
    }
    if (!prepTimeLocal || !expiryTimeLocal) {
      setFormError('Both preparation time and expiry time are mandatory');
      return;
    }

    const prepInstant = new Date(prepTimeLocal).toISOString();
    const expiryInstant = new Date(expiryTimeLocal).toISOString();

    if (new Date(expiryInstant) <= new Date(prepInstant)) {
      setFormError('Expiry time must be strictly after preparation time');
      return;
    }

    setIsSubmitting(true);
    setFormError(null);

    try {
      if (editingDonation) {
        const payload: UpdateDonationRequest = {
          title: title.trim(),
          category,
          totalQuantity: qtyNum,
          quantityUnit,
          dietaryType,
          storageCondition,
          preparationTime: prepInstant,
          expiryTime: expiryInstant,
          pickupNotes: pickupNotes.trim() || undefined,
        };
        await api.updateDonation(editingDonation.id, payload);
        setSuccessMessage(`Listing "${title}" updated successfully.`);
      } else {
        const payload: CreateDonationRequest = {
          title: title.trim(),
          category,
          totalQuantity: qtyNum,
          quantityUnit,
          dietaryType,
          storageCondition,
          preparationTime: prepInstant,
          expiryTime: expiryInstant,
          pickupNotes: pickupNotes.trim() || undefined,
          publishImmediately,
        };
        await api.createDonation(payload);
        setSuccessMessage(
          publishImmediately
            ? `Food donation batch published successfully! Visible immediately for NGO requests.`
            : `Food donation draft saved. You can publish it anytime when the kitchen batch is confirmed.`
        );
      }
      setIsModalOpen(false);
      fetchMyDonations();
      setTimeout(() => setSuccessMessage(null), 5000);
    } catch (err: any) {
      setFormError(err.message || 'Operation failed. Please review form inputs.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePublish = async (id: number, batchTitle: string) => {
    try {
      await api.publishDonation(id);
      setSuccessMessage(`"${batchTitle}" is now published and AVAILABLE for NGO allocations!`);
      fetchMyDonations();
      setTimeout(() => setSuccessMessage(null), 4000);
    } catch (err: any) {
      setError(err.message || 'Failed to publish listing.');
    }
  };

  const handleCancel = async (id: number, batchTitle: string) => {
    if (!window.confirm(`Are you sure you want to cancel the listing "${batchTitle}"?`)) {
      return;
    }
    try {
      await api.cancelDonation(id);
      setSuccessMessage(`Listing "${batchTitle}" has been cancelled.`);
      fetchMyDonations();
      setTimeout(() => setSuccessMessage(null), 4000);
    } catch (err: any) {
      setError(err.message || 'Failed to cancel listing.');
    }
  };

  // Status Filter
  const filteredDonations = donations.filter((item) => {
    if (statusFilter === 'ALL') return true;
    if (statusFilter === 'ACTIVE') return item.status === 'AVAILABLE' || item.status === 'PARTIALLY_ALLOCATED';
    if (statusFilter === 'DRAFT') return item.status === 'DRAFT';
    if (statusFilter === 'COMPLETED') return item.status === 'COMPLETED';
    if (statusFilter === 'CANCELLED') return item.status === 'CANCELLED' || item.status === 'EXPIRED';
    return true;
  });

  const getStatusBadge = (status: DonationStatus) => {
    switch (status) {
      case 'DRAFT':
        return <Badge variant="neutral">Draft (Private)</Badge>;
      case 'AVAILABLE':
        return <Badge variant="available">Active Available</Badge>;
      case 'PARTIALLY_ALLOCATED':
        return <Badge variant="allocated">Partially Claimed</Badge>;
      case 'FULLY_ALLOCATED':
        return <Badge variant="allocated">Fully Allocated</Badge>;
      case 'COMPLETED':
        return <Badge variant="neutral">Distributed</Badge>;
      case 'CANCELLED':
        return <Badge variant="warning">Cancelled</Badge>;
      case 'EXPIRED':
        return <Badge variant="warning">Expired</Badge>;
      default:
        return <Badge variant="neutral">{status}</Badge>;
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Top Banner: Donor Organization Context */}
      <div className="bg-surface border border-border rounded-2xl p-6 mb-8 shadow-card flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-start gap-4">
          <div className="w-14 h-14 rounded-2xl bg-forest/10 text-forest flex items-center justify-center shrink-0">
            <ChefHat className="w-8 h-8" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-extrabold text-ink-primary tracking-tight">
                {user?.fullName || 'Commercial Food Donor Console'}
              </h1>
              <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 border border-emerald-300">
                Verified Kitchen
              </span>
            </div>
            <p className="text-xs text-ink-secondary mt-1 flex items-center gap-3">
              <span className="flex items-center gap-1">
                <MapPin className="w-3.5 h-3.5 text-forest" />
                {user?.city || 'Bengaluru'} Operational Zone
              </span>
              <span>•</span>
              <span className="flex items-center gap-1 text-emerald-700">
                <ShieldCheck className="w-3.5 h-3.5" />
                FSSAI Compliant Food Redistribution
              </span>
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            size="sm"
            onClick={fetchMyDonations}
            disabled={isLoading}
            leftIcon={<RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />}
          >
            Refresh
          </Button>
          <Button
            variant="primary"
            size="md"
            onClick={openCreateModal}
            leftIcon={<Plus className="w-4 h-4" />}
          >
            Create Food Donation
          </Button>
        </div>
      </div>

      {/* Feedback Banners */}
      {successMessage && (
        <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-800 flex items-start gap-2.5 shadow-xs">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
          <div>
            <span className="font-bold block">Action Confirmed</span>
            <span className="text-[12px]">{successMessage}</span>
          </div>
        </div>
      )}

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2.5 shadow-xs">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
          <div>
            <span className="font-bold block">Operation Notice</span>
            <span className="text-[12px]">{error}</span>
          </div>
        </div>
      )}

      {/* KPI Counters */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <Card padding="md" className="bg-surface shadow-xs border-border">
          <span className="text-xs text-ink-secondary font-medium">Total Batches Listed</span>
          <div className="text-2xl font-extrabold text-ink-primary mt-1">{donations.length}</div>
        </Card>
        <Card padding="md" className="bg-surface shadow-xs border-border">
          <span className="text-xs text-emerald-700 font-medium">Active Available</span>
          <div className="text-2xl font-extrabold text-forest mt-1">
            {donations.filter((d) => d.status === 'AVAILABLE' || d.status === 'PARTIALLY_ALLOCATED').length}
          </div>
        </Card>
        <Card padding="md" className="bg-surface shadow-xs border-border">
          <span className="text-xs text-amber-700 font-medium">Draft Listings</span>
          <div className="text-2xl font-extrabold text-amber-700 mt-1">
            {donations.filter((d) => d.status === 'DRAFT').length}
          </div>
        </Card>
        <Card padding="md" className="bg-surface shadow-xs border-border">
          <span className="text-xs text-ink-secondary font-medium">Successfully Distributed</span>
          <div className="text-2xl font-extrabold text-ink-primary mt-1">
            {donations.filter((d) => d.status === 'COMPLETED').length}
          </div>
        </Card>
      </div>

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 mb-6 border-b border-border pb-3 overflow-x-auto">
        {[
          { key: 'ALL', label: 'All Listings' },
          { key: 'ACTIVE', label: 'Active & Available' },
          { key: 'DRAFT', label: 'Drafts' },
          { key: 'COMPLETED', label: 'Distributed' },
          { key: 'CANCELLED', label: 'Cancelled / Expired' },
        ].map((tab) => (
          <button
            key={tab.key}
            onClick={() => setStatusFilter(tab.key)}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
              statusFilter === tab.key
                ? 'bg-forest text-white shadow-xs'
                : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Main Listings Grid */}
      {isLoading ? (
        <div className="py-20 text-center">
          <RefreshCw className="w-8 h-8 text-forest animate-spin mx-auto mb-3" />
          <p className="text-sm font-medium text-ink-secondary">Loading donation listings from PostgreSQL...</p>
        </div>
      ) : filteredDonations.length === 0 ? (
        <Card padding="lg" className="text-center py-16 bg-surface shadow-card border-dashed">
          <Utensils className="w-12 h-12 text-sage mx-auto mb-3 opacity-60" />
          <h3 className="text-lg font-bold text-ink-primary">No Food Donation Listings Found</h3>
          <p className="text-xs text-ink-secondary max-w-md mx-auto mt-1 mb-6">
            You currently have no batches in this filter. Post surplus edible food from your kitchen to connect with verified welfare NGOs.
          </p>
          <Button variant="primary" size="md" onClick={openCreateModal} leftIcon={<Plus className="w-4 h-4" />}>
            Create First Food Donation
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredDonations.map((item) => (
            <Card
              key={item.id}
              padding="lg"
              className="bg-surface border border-border shadow-card hover:shadow-card-hover transition-all flex flex-col justify-between"
            >
              <div className="space-y-4">
                {/* Header & Status */}
                <div className="flex items-start justify-between gap-2">
                  <span className="text-[11px] font-bold uppercase tracking-wider text-forest px-2 py-0.5 rounded bg-forest-subtle">
                    {item.category.replace('_', ' ')}
                  </span>
                  {getStatusBadge(item.status)}
                </div>

                {/* Title */}
                <h3 className="text-base font-extrabold text-ink-primary line-clamp-2">
                  {item.title}
                </h3>

                {/* Quantity and Dietary Indicator */}
                <div className="p-3 bg-warm-muted rounded-xl space-y-2 text-xs">
                  <div className="flex items-center justify-between">
                    <span className="text-ink-secondary">Available Stock:</span>
                    <span className="font-extrabold text-forest text-sm">
                      {item.availableQuantity} / {item.totalQuantity} {item.quantityUnit}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-[11px]">
                    <span className="text-ink-secondary">Dietary:</span>
                    <span className="font-semibold text-ink-primary">{item.dietaryType.replace('_', ' ')}</span>
                  </div>

                  <div className="flex items-center justify-between text-[11px]">
                    <span className="text-ink-secondary">Storage:</span>
                    <span className="font-semibold text-ink-primary">{item.storageCondition.replace('_', ' ')}</span>
                  </div>
                </div>

                {/* Timing */}
                <div className="text-[11px] text-ink-secondary space-y-1">
                  <div className="flex items-center gap-1.5">
                    <Clock className="w-3.5 h-3.5 text-forest" />
                    <span>Expires: {new Date(item.expiryTime).toLocaleString()}</span>
                  </div>
                  {item.pickupNotes && (
                    <p className="text-[11px] text-ink-secondary line-clamp-1 italic">
                      "{item.pickupNotes}"
                    </p>
                  )}
                </div>
              </div>

              {/* Action Buttons */}
              <div className="pt-4 border-t border-border mt-4 flex items-center justify-between gap-2">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setDetailDonation(item)}
                  className="text-xs"
                  leftIcon={<Eye className="w-3.5 h-3.5" />}
                >
                  Details
                </Button>

                <div className="flex items-center gap-1.5">
                  {item.status === 'DRAFT' && (
                    <>
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => handlePublish(item.id, item.title)}
                        leftIcon={<Send className="w-3.5 h-3.5" />}
                      >
                        Publish
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => openEditModal(item)}
                        leftIcon={<Edit3 className="w-3.5 h-3.5" />}
                      >
                        Edit
                      </Button>
                    </>
                  )}

                  {item.status === 'AVAILABLE' && (
                    <>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => openEditModal(item)}
                        leftIcon={<Edit3 className="w-3.5 h-3.5" />}
                      >
                        Edit
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleCancel(item.id, item.title)}
                        className="text-red-600 hover:bg-red-50"
                        leftIcon={<XCircle className="w-3.5 h-3.5" />}
                      >
                        Cancel
                      </Button>
                    </>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Create / Edit Donation Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-surface border border-border rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto p-6 shadow-2xl">
            <div className="flex items-center justify-between border-b border-border pb-4 mb-4">
              <div>
                <h2 className="text-xl font-extrabold text-ink-primary">
                  {editingDonation ? 'Edit Food Listing' : 'Create New Food Donation'}
                </h2>
                <p className="text-xs text-ink-secondary">
                  Fill in precise kitchen specifications to ensure safe redistribution by partner NGOs
                </p>
              </div>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg hover:bg-warm-muted text-ink-secondary"
              >
                ✕
              </button>
            </div>

            {formError && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2">
                <AlertCircle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
                <span>{formError}</span>
              </div>
            )}

            <form onSubmit={handleFormSubmit} className="space-y-4">
              <Input
                label="Food Title / Meal Batch Description"
                placeholder="e.g. Fresh Vegetable Biryani & Raita (50 Boxes)"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Food Category
                  </label>
                  <select
                    value={category}
                    onChange={(e) => setCategory(e.target.value as FoodCategory)}
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  >
                    <option value="COOKED_MEALS">Cooked Meals</option>
                    <option value="BREADS_ROTIS">Breads & Rotis</option>
                    <option value="DAIRY_SWEETS">Dairy & Sweets</option>
                    <option value="RAW_PRODUCE">Raw Produce / Veggies</option>
                    <option value="PACKAGED_GOODS">Packaged Dry Goods</option>
                  </select>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <Input
                    label="Quantity"
                    type="number"
                    step="0.5"
                    min="1"
                    value={totalQuantity}
                    onChange={(e) => setTotalQuantity(e.target.value)}
                    required
                  />
                  <div className="space-y-1.5">
                    <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                      Unit
                    </label>
                    <select
                      value={quantityUnit}
                      onChange={(e) => setQuantityUnit(e.target.value as QuantityUnit)}
                      className="w-full rounded-lg border border-border bg-surface py-2.5 px-2 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                    >
                      <option value="MEAL_BOXES">Meal Boxes</option>
                      <option value="KG">Kilograms (kg)</option>
                      <option value="PORTIONS">Portions</option>
                      <option value="LITERS">Liters</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Dietary Classification
                  </label>
                  <select
                    value={dietaryType}
                    onChange={(e) => setDietaryType(e.target.value as DietaryType)}
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  >
                    <option value="PURE_VEG">Pure Vegetarian</option>
                    <option value="NON_VEG">Non-Vegetarian</option>
                    <option value="VEGAN">Strict Vegan</option>
                  </select>
                </div>

                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Storage Condition
                  </label>
                  <select
                    value={storageCondition}
                    onChange={(e) => setStorageCondition(e.target.value as StorageCondition)}
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  >
                    <option value="HOT_INSULATED">Hot Insulated (&gt; 65°C)</option>
                    <option value="REFRIGERATED">Refrigerated (&lt; 4°C)</option>
                    <option value="AMBIENT">Ambient Room Temperature</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Preparation Timestamp
                  </label>
                  <input
                    type="datetime-local"
                    value={prepTimeLocal}
                    onChange={(e) => setPrepTimeLocal(e.target.value)}
                    required
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                    Safe Expiry Timestamp
                  </label>
                  <input
                    type="datetime-local"
                    value={expiryTimeLocal}
                    onChange={(e) => setExpiryTimeLocal(e.target.value)}
                    required
                    className="w-full rounded-lg border border-border bg-surface py-2.5 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="block text-xs font-semibold uppercase tracking-wider text-ink-primary">
                  Kitchen Pickup Instructions & Packaging Notes
                </label>
                <textarea
                  value={pickupNotes}
                  onChange={(e) => setPickupNotes(e.target.value)}
                  placeholder="e.g. Kitchen rear service gate; please carry insulated carrier bags; contact Chef Anand at reception."
                  rows={3}
                  className="w-full rounded-lg border border-border bg-surface py-2 px-3 text-sm text-ink-primary focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                />
              </div>

              {!editingDonation && (
                <label className="flex items-center gap-2 p-3 bg-forest-subtle rounded-xl text-xs text-forest-dark font-medium cursor-pointer">
                  <input
                    type="checkbox"
                    checked={publishImmediately}
                    onChange={(e) => setPublishImmediately(e.target.checked)}
                    className="rounded border-border text-forest focus:ring-forest w-4 h-4"
                  />
                  <span>
                    Publish immediately (Make available to registered NGOs right now). If unchecked, saves as private draft.
                  </span>
                </label>
              )}

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-border">
                <Button variant="ghost" size="md" type="button" onClick={() => setIsModalOpen(false)}>
                  Cancel
                </Button>
                <Button variant="primary" size="md" type="submit" isLoading={isSubmitting}>
                  {editingDonation ? 'Save Updates' : publishImmediately ? 'Publish to Network' : 'Save as Draft'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Batch Details View Modal */}
      {detailDonation && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-surface border border-border rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-start justify-between border-b border-border pb-3">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-forest px-2 py-0.5 rounded bg-forest-subtle">
                  {detailDonation.category.replace('_', ' ')}
                </span>
                <h3 className="text-lg font-extrabold text-ink-primary mt-1">{detailDonation.title}</h3>
              </div>
              <button
                onClick={() => setDetailDonation(null)}
                className="p-1 rounded-lg hover:bg-warm-muted text-ink-secondary"
              >
                ✕
              </button>
            </div>

            <div className="space-y-3 text-xs text-ink-secondary">
              <div className="grid grid-cols-2 gap-2 p-3 bg-warm-muted rounded-xl text-ink-primary font-medium">
                <div>
                  <span className="text-ink-secondary text-[11px] block">Total Prepared:</span>
                  {detailDonation.totalQuantity} {detailDonation.quantityUnit}
                </div>
                <div>
                  <span className="text-ink-secondary text-[11px] block">Current Available:</span>
                  <span className="text-forest font-bold">{detailDonation.availableQuantity} {detailDonation.quantityUnit}</span>
                </div>
              </div>

              <div>
                <span className="font-semibold text-ink-primary">Lifecycle Status:</span>{' '}
                {detailDonation.statusDescription}
              </div>

              <div>
                <span className="font-semibold text-ink-primary">Storage Specifications:</span>{' '}
                {detailDonation.storageCondition.replace('_', ' ')} ({detailDonation.dietaryType.replace('_', ' ')})
              </div>

              <div>
                <span className="font-semibold text-ink-primary">Prepared At:</span>{' '}
                {new Date(detailDonation.preparationTime).toLocaleString()}
              </div>

              <div>
                <span className="font-semibold text-ink-primary">Safe Expiry:</span>{' '}
                {new Date(detailDonation.expiryTime).toLocaleString()}
              </div>

              {detailDonation.pickupNotes && (
                <div className="p-3 bg-warm-bg rounded-lg border border-border">
                  <span className="font-bold text-ink-primary block mb-1">Pickup Notes:</span>
                  <p className="text-[11px] text-ink-primary">{detailDonation.pickupNotes}</p>
                </div>
              )}
            </div>

            <div className="pt-3 border-t border-border text-right">
              <Button variant="outline" size="sm" onClick={() => setDetailDonation(null)}>
                Close
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
