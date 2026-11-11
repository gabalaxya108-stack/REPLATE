import React, { useState, useEffect, useCallback } from 'react';
import { api, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DonationResponse, CreateDonationRequest, UpdateDonationRequest } from '../types/donation';
import { FoodRequestResponse } from '../types/request';
import { AllocationResponse } from '../types/allocation';
import { DeliveryTaskResponse, VolunteerSummary } from '../types/delivery';
import { FoodCategory, QuantityUnit, DietaryType, StorageCondition, DonationStatus } from '../types';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Input } from '../components/common/Input';
import { 
  Plus, Edit3, Send, XCircle, Clock, Utensils, AlertCircle, 
  CheckCircle2, RefreshCw, Eye, MapPin, ChefHat, ShieldCheck,
  Building2, Check, MessageSquare, PackageCheck, Truck, KeyRound, User, Phone
} from 'lucide-react';

export const DonorDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeMainTab, setActiveMainTab] = useState<'DONATIONS' | 'CLAIMS' | 'DELIVERIES'>('DONATIONS');

  const [donations, setDonations] = useState<DonationResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Incoming Requests State
  const [receivedRequests, setReceivedRequests] = useState<FoodRequestResponse[]>([]);
  const [isLoadingRequests, setIsLoadingRequests] = useState(false);
  const [claimsStatusFilter, setClaimsStatusFilter] = useState<string>('ALL');
  const [approvingRequestId, setApprovingRequestId] = useState<number | null>(null);

  // Reject Modal State
  const [rejectingRequest, setRejectingRequest] = useState<FoodRequestResponse | null>(null);
  const [rejectionReason, setRejectionReason] = useState<string>('');
  const [isSubmittingReject, setIsSubmittingReject] = useState<boolean>(false);
  const [rejectError, setRejectError] = useState<string | null>(null);

  // Allocation Engine State
  const [allocations, setAllocations] = useState<AllocationResponse[]>([]);
  const [deliveries, setDeliveries] = useState<DeliveryTaskResponse[]>([]);
  const [volunteers, setVolunteers] = useState<VolunteerSummary[]>([]);
  const [allocatingRequest, setAllocatingRequest] = useState<FoodRequestResponse | null>(null);
  const [allocQuantity, setAllocQuantity] = useState<string>('');
  const [allocNotes, setAllocNotes] = useState<string>('');
  const [isSubmittingAlloc, setIsSubmittingAlloc] = useState<boolean>(false);
  const [allocError, setAllocError] = useState<string | null>(null);

  // Volunteer Assignment Modal State
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [assigningAllocationId, setAssigningAllocationId] = useState<number | null>(null);
  const [selectedVolunteerId, setSelectedVolunteerId] = useState<number | null>(null);
  const [assignNotes, setAssignNotes] = useState<string>('');
  const [isSubmittingAssign, setIsSubmittingAssign] = useState<boolean>(false);
  const [assignError, setAssignError] = useState<string | null>(null);

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

  const fetchReceivedRequests = useCallback(async () => {
    setIsLoadingRequests(true);
    try {
      const res = await api.getReceivedFoodRequests(0, 50);
      setReceivedRequests(res.content);
    } catch (err: any) {
      console.error('Failed to load incoming NGO claims:', err);
    } finally {
      setIsLoadingRequests(false);
    }
  }, []);

  const fetchMyAllocations = useCallback(async () => {
    try {
      const [resAlloc, resDeliv, resVols] = await Promise.all([
        api.getMyAllocations(0, 100),
        api.getMyDeliveries().catch(() => []),
        api.getAvailableVolunteers().catch(() => []),
      ]);
      setAllocations(resAlloc.content);
      setDeliveries(resDeliv);
      setVolunteers(resVols);
    } catch (err: any) {
      console.error('Failed to load allocations and logistics:', err);
    }
  }, []);

  const handleOpenAssignModal = (allocationId: number) => {
    setAssigningAllocationId(allocationId);
    setSelectedVolunteerId(volunteers.length > 0 ? volunteers[0].userId : null);
    setAssignNotes('');
    setAssignError(null);
    setIsAssignModalOpen(true);
  };

  const handleConfirmAssignment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!assigningAllocationId || !selectedVolunteerId) return;

    setIsSubmittingAssign(true);
    setAssignError(null);
    try {
      await api.assignVolunteer({
        allocationId: assigningAllocationId,
        volunteerId: selectedVolunteerId,
        notes: assignNotes.trim() || undefined,
      });
      setIsAssignModalOpen(false);
      setSuccessMessage('Volunteer courier assigned! Delivery dispatched to driver.');
      fetchMyAllocations();
      setTimeout(() => setSuccessMessage(null), 5000);
    } catch (err: any) {
      setAssignError(err instanceof ApiError ? err.message : 'Failed to assign volunteer.');
    } finally {
      setIsSubmittingAssign(false);
    }
  };

  useEffect(() => {
    fetchMyDonations();
    fetchReceivedRequests();
    fetchMyAllocations();
  }, [fetchMyDonations, fetchReceivedRequests, fetchMyAllocations]);

  const allocatedByRequestId = React.useMemo(() => {
    const map: Record<number, number> = {};
    for (const a of allocations) {
      if (a.status !== 'CANCELLED') {
        map[a.requestId] = (map[a.requestId] || 0) + Number(a.allocatedQuantity);
      }
    }
    return map;
  }, [allocations]);

  const handleOpenAllocModal = (req: FoodRequestResponse) => {
    setAllocatingRequest(req);
    const allocated = allocatedByRequestId[req.id] || 0;
    const remainingReq = Math.max(0, req.requestedQuantity - allocated);
    const matchingDonation = donations.find(d => d.id === req.donationId);
    const availStock = matchingDonation ? matchingDonation.availableQuantity : req.donationAvailableQuantity;
    const defaultQty = Math.min(remainingReq, availStock);
    setAllocQuantity(defaultQty > 0 ? defaultQty.toString() : '');
    setAllocNotes('');
    setAllocError(null);
  };

  const handleConfirmAllocation = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!allocatingRequest) return;

    const qty = parseFloat(allocQuantity);
    if (isNaN(qty) || qty <= 0) {
      setAllocError('Allocation quantity must be strictly greater than zero.');
      return;
    }

    setIsSubmittingAlloc(true);
    setAllocError(null);
    try {
      await api.createAllocation({
        requestId: allocatingRequest.id,
        allocatedQuantity: qty,
        notes: allocNotes.trim() || undefined,
      });
      setAllocatingRequest(null);
      setSuccessMessage(`Inventory committed! ${qty} ${allocatingRequest.donationQuantityUnit} allocated to ${allocatingRequest.ngoName}. Stock deducted in PostgreSQL.`);
      fetchMyDonations();
      fetchReceivedRequests();
      fetchMyAllocations();
      setTimeout(() => setSuccessMessage(null), 6000);
    } catch (err: any) {
      setAllocError(err instanceof ApiError ? err.message : 'Failed to allocate food inventory.');
    } finally {
      setIsSubmittingAlloc(false);
    }
  };

  const handleApproveRequest = async (id: number) => {
    setApprovingRequestId(id);
    setError(null);
    try {
      await api.approveFoodRequest(id);
      setSuccessMessage('Food request approved! Allocation and volunteer dispatch will proceed in Step 7.');
      fetchReceivedRequests();
      fetchMyDonations();
      setTimeout(() => setSuccessMessage(null), 5000);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Failed to approve food request.');
    } finally {
      setApprovingRequestId(null);
    }
  };

  const handleOpenRejectModal = (req: FoodRequestResponse) => {
    setRejectingRequest(req);
    setRejectionReason('');
    setRejectError(null);
  };

  const handleConfirmReject = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!rejectingRequest) return;
    if (!rejectionReason.trim() || rejectionReason.trim().length < 5) {
      setRejectError('Please state a valid justification for declining this claim (minimum 5 characters).');
      return;
    }

    setIsSubmittingReject(true);
    setRejectError(null);
    try {
      await api.rejectFoodRequest(rejectingRequest.id, {
        rejectionReason: rejectionReason.trim(),
      });
      setRejectingRequest(null);
      setSuccessMessage('Food request declined with notice delivered to the requesting NGO.');
      fetchReceivedRequests();
      setTimeout(() => setSuccessMessage(null), 5000);
    } catch (err: any) {
      setRejectError(err instanceof ApiError ? err.message : 'Failed to decline request.');
    } finally {
      setIsSubmittingReject(false);
    }
  };

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

  const pendingClaimsCount = receivedRequests.filter((r) => r.status === 'PENDING').length;
  const approvedClaimsCount = receivedRequests.filter((r) => r.status === 'APPROVED').length;
  const rejectedClaimsCount = receivedRequests.filter((r) => r.status === 'REJECTED').length;

  const filteredClaims = receivedRequests.filter((r) => {
    if (claimsStatusFilter === 'ALL') return true;
    return r.status === claimsStatusFilter;
  });

  const getRequestStatusBadge = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Badge variant="warning">Under Review</Badge>;
      case 'APPROVED':
        return <Badge variant="available">Approved</Badge>;
      case 'REJECTED':
        return <Badge variant="non-veg">Declined</Badge>;
      case 'WITHDRAWN':
        return <Badge variant="neutral">Withdrawn</Badge>;
      case 'EXPIRED':
        return <Badge variant="neutral">Expired</Badge>;
      default:
        return <Badge variant="neutral">{status}</Badge>;
    }
  };

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

      {/* Primary Dashboard Navigation Tabs */}
      <div className="flex items-center gap-3 mb-8 border-b border-border pb-4">
        <button
          onClick={() => setActiveMainTab('DONATIONS')}
          className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-sm font-extrabold transition-all ${
            activeMainTab === 'DONATIONS'
              ? 'bg-forest text-white shadow-xs'
              : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
          }`}
        >
          <Utensils className="w-4 h-4" />
          <span>Food Batches & Stock</span>
          <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${
            activeMainTab === 'DONATIONS' ? 'bg-white/20 text-white' : 'bg-warm-muted text-ink-primary'
          }`}>
            {donations.length}
          </span>
        </button>

        <button
          onClick={() => setActiveMainTab('CLAIMS')}
          className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-sm font-extrabold transition-all ${
            activeMainTab === 'CLAIMS'
              ? 'bg-forest text-white shadow-xs'
              : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
          }`}
        >
          <Building2 className="w-4 h-4" />
          <span>Incoming NGO Claims</span>
          {pendingClaimsCount > 0 ? (
            <span className="text-[10px] px-2 py-0.5 rounded-full font-black bg-amber-500 text-white animate-pulse">
              {pendingClaimsCount} Pending
            </span>
          ) : (
            <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${
              activeMainTab === 'CLAIMS' ? 'bg-white/20 text-white' : 'bg-warm-muted text-ink-primary'
            }`}>
              {receivedRequests.length}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveMainTab('DELIVERIES')}
          className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-sm font-extrabold transition-all ${
            activeMainTab === 'DELIVERIES'
              ? 'bg-forest text-white shadow-xs'
              : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
          }`}
        >
          <Truck className="w-4 h-4" />
          <span>Logistics & Dispatch</span>
          <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${
            activeMainTab === 'DELIVERIES' ? 'bg-white/20 text-white' : 'bg-warm-muted text-ink-primary'
          }`}>
            {deliveries.length}
          </span>
        </button>
      </div>

      {activeMainTab === 'DONATIONS' && (
        <>
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
        </>
      )}

      {/* Main Content Area: Incoming NGO Claims Tab */}
      {activeMainTab === 'CLAIMS' && (
        <>
          {/* Claims KPI Counters */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-ink-secondary font-medium">Claims Received</span>
              <div className="text-2xl font-extrabold text-ink-primary mt-1">{receivedRequests.length}</div>
              <span className="text-[11px] text-ink-muted">From registered NGO partners</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-amber-700 font-medium">Under Review</span>
              <div className="text-2xl font-extrabold text-amber-700 mt-1">{pendingClaimsCount}</div>
              <span className="text-[11px] text-amber-600/80">Pending your approval</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-emerald-700 font-medium">Approved Claims</span>
              <div className="text-2xl font-extrabold text-forest mt-1">{approvedClaimsCount}</div>
              <span className="text-[11px] text-emerald-600/80">Stock confirmed for pickup</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-ink-secondary font-medium">Declined / Withdrawn</span>
              <div className="text-2xl font-extrabold text-ink-secondary mt-1">{rejectedClaimsCount}</div>
              <span className="text-[11px] text-ink-muted">Unfulfilled requests</span>
            </Card>
          </div>

          {/* Claims Filter Tabs */}
          <div className="flex items-center gap-2 mb-6 border-b border-border pb-3 overflow-x-auto">
            {[
              { key: 'ALL', label: 'All Incoming Claims' },
              { key: 'PENDING', label: 'Under Review' },
              { key: 'APPROVED', label: 'Approved' },
              { key: 'REJECTED', label: 'Declined' },
              { key: 'WITHDRAWN', label: 'Withdrawn' },
            ].map((tab) => (
              <button
                key={tab.key}
                onClick={() => setClaimsStatusFilter(tab.key)}
                className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
                  claimsStatusFilter === tab.key
                    ? 'bg-forest text-white shadow-xs'
                    : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Claims List */}
          {isLoadingRequests ? (
            <div className="py-20 text-center">
              <RefreshCw className="w-8 h-8 text-forest animate-spin mx-auto mb-3" />
              <p className="text-sm font-medium text-ink-secondary">Loading incoming NGO claims from PostgreSQL...</p>
            </div>
          ) : filteredClaims.length === 0 ? (
            <Card padding="lg" className="text-center py-16 bg-surface shadow-card border-dashed">
              <Building2 className="w-12 h-12 text-sage mx-auto mb-3 opacity-60" />
              <h3 className="text-lg font-bold text-ink-primary">No Incoming NGO Claims Found</h3>
              <p className="text-xs text-ink-secondary max-w-md mx-auto mt-1 mb-4">
                {claimsStatusFilter === 'ALL'
                  ? 'No welfare NGOs have submitted claims for your surplus batches yet. Active batches appear in the public feed for NGOs to request.'
                  : `No incoming claims currently matching status "${claimsStatusFilter}".`}
              </p>
            </Card>
          ) : (
            <div className="space-y-4">
              {filteredClaims.map((claim) => (
                <Card
                  key={claim.id}
                  padding="lg"
                  className="bg-surface border border-border shadow-card flex flex-col md:flex-row md:items-start justify-between gap-5"
                >
                  <div className="space-y-3 flex-1">
                    <div className="flex items-center gap-3 flex-wrap">
                      <span className="text-base font-extrabold text-ink-primary">
                        {claim.donationTitle}
                      </span>
                      {getRequestStatusBadge(claim.status)}
                      <span className="text-xs text-ink-secondary flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-ink-muted" />
                        Received {new Date(claim.createdAt).toLocaleString()}
                      </span>
                    </div>

                    {/* NGO Identity & Verification Box */}
                    <div className="p-3.5 bg-warm-muted/70 rounded-xl space-y-2 text-xs border border-border/60">
                      <div className="flex items-center justify-between flex-wrap gap-2">
                        <div className="flex items-center gap-2">
                          <Building2 className="w-4 h-4 text-forest" />
                          <span className="font-extrabold text-ink-primary text-sm">
                            {claim.ngoName}
                          </span>
                        </div>
                        {claim.ngoDarpanId && (
                          <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-blue-100 text-blue-800 border border-blue-200">
                            Darpan ID: {claim.ngoDarpanId}
                          </span>
                        )}
                      </div>

                      <div className="flex items-center gap-4 text-ink-secondary text-[11px] pt-1">
                        {claim.ngoPhone && (
                          <span>
                            <strong>Contact:</strong> {claim.ngoPhone}
                          </span>
                        )}
                        <span>•</span>
                        <span>
                          <strong>Requested:</strong>{' '}
                          <span className="font-extrabold text-forest text-xs">
                            {claim.requestedQuantity} {claim.donationQuantityUnit}
                          </span>
                        </span>
                        <span>•</span>
                        <span>
                          <strong>Batch Available:</strong> {claim.donationAvailableQuantity} {claim.donationQuantityUnit}
                        </span>
                      </div>
                    </div>

                    {/* Beneficiary Justification */}
                    <div className="p-3 bg-surface rounded-xl border border-border text-xs space-y-1">
                      <span className="font-bold text-ink-primary flex items-center gap-1.5 text-[11px] uppercase tracking-wider">
                        <MessageSquare className="w-3.5 h-3.5 text-forest" />
                        NGO Beneficiary & Distribution Plan
                      </span>
                      <p className="text-ink-secondary leading-relaxed pl-5">
                        "{claim.intendedBeneficiaries}"
                      </p>
                    </div>

                    {/* Rejection notice if declined */}
                    {claim.status === 'REJECTED' && claim.rejectionReason && (
                      <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700">
                        <span className="font-bold block">Decline Justification:</span>
                        <p className="mt-0.5">{claim.rejectionReason}</p>
                      </div>
                    )}
                  </div>

                  {/* Action Column */}
                  <div className="flex md:flex-col items-center md:items-end justify-end gap-2.5 shrink-0 pt-3 md:pt-0 border-t md:border-t-0 border-border">
                    {claim.status === 'PENDING' && (
                      <>
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => handleApproveRequest(claim.id)}
                          disabled={approvingRequestId === claim.id}
                          leftIcon={
                            approvingRequestId === claim.id ? (
                              <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                            ) : (
                              <Check className="w-3.5 h-3.5" />
                            )
                          }
                        >
                          {approvingRequestId === claim.id ? 'Approving...' : 'Approve Claim'}
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleOpenRejectModal(claim)}
                          disabled={approvingRequestId === claim.id}
                          className="text-red-700 border-red-200 hover:bg-red-50 hover:border-red-300"
                          leftIcon={<XCircle className="w-3.5 h-3.5 text-red-500" />}
                        >
                          Decline
                        </Button>
                      </>
                    )}

                    {claim.status === 'APPROVED' && (() => {
                      const allocated = allocatedByRequestId[claim.id] || 0;
                      const remainingReq = Math.max(0, claim.requestedQuantity - allocated);
                      const matchingDonation = donations.find(d => d.id === claim.donationId);
                      const availStock = matchingDonation ? matchingDonation.availableQuantity : claim.donationAvailableQuantity;
                      const isFullyAllocated = remainingReq === 0;

                      return (
                        <div className="flex flex-col items-end gap-1.5">
                          <div className="flex items-center gap-2">
                            {isFullyAllocated ? (
                              <span className="text-xs font-bold text-emerald-800 bg-emerald-100 border border-emerald-300 px-3 py-1.5 rounded-lg flex items-center gap-1.5">
                                <CheckCircle2 className="w-4 h-4 text-emerald-700" />
                                Fully Allocated ({allocated} {claim.donationQuantityUnit})
                              </span>
                            ) : (
                              <div className="text-right">
                                <span className="text-xs font-bold text-blue-800 bg-blue-100 border border-blue-300 px-2.5 py-1 rounded-md block mb-1">
                                  Approved: {allocated > 0 ? `${allocated} / ${claim.requestedQuantity}` : `${claim.requestedQuantity}`} {claim.donationQuantityUnit}
                                </span>
                                <Button
                                  variant="primary"
                                  size="sm"
                                  onClick={() => handleOpenAllocModal(claim)}
                                  disabled={availStock <= 0}
                                  leftIcon={<PackageCheck className="w-4 h-4" />}
                                >
                                  {allocated > 0 ? 'Allocate Remaining' : 'Allocate Stock'}
                                </Button>
                              </div>
                            )}
                          </div>
                          <span className="text-[11px] text-ink-secondary">
                            {isFullyAllocated
                              ? 'Inventory committed; awaiting volunteer assignment'
                              : availStock > 0
                                ? `Batch stock available: ${availStock} ${claim.donationQuantityUnit}`
                                : 'Donation inventory currently exhausted'}
                          </span>
                        </div>
                      );
                    })()}

                    {claim.status === 'REJECTED' && (
                      <span className="text-xs font-medium text-red-700 bg-red-50 border border-red-200 px-3 py-1 rounded-lg">
                        Claim Declined
                      </span>
                    )}

                    {claim.status === 'WITHDRAWN' && (
                      <span className="text-xs font-medium text-ink-muted bg-warm-muted border border-border px-3 py-1 rounded-lg">
                        Withdrawn by NGO
                      </span>
                    )}
                  </div>
                </Card>
              ))}
            </div>
          )}
        </>
      )}

      {/* Main Content Area: Logistics & Courier Dispatch Tab */}
      {activeMainTab === 'DELIVERIES' && (
        <>
          {/* Logistics KPI Counters */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-ink-secondary font-medium">Total Deliveries</span>
              <div className="text-2xl font-extrabold text-ink-primary mt-1">{deliveries.length}</div>
              <span className="text-[11px] text-ink-muted">All dispatched allocations</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-amber-700 font-medium">Pending Driver</span>
              <div className="text-2xl font-extrabold text-amber-700 mt-1">
                {deliveries.filter(d => d.status === 'UNASSIGNED' || d.status === 'PENDING_ASSIGNMENT' || d.status === 'REJECTED').length}
              </div>
              <span className="text-[11px] text-amber-600/80">Awaiting volunteer assignment</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-blue-700 font-medium">In Transit</span>
              <div className="text-2xl font-extrabold text-blue-700 mt-1">
                {deliveries.filter(d => d.status === 'IN_TRANSIT' || d.status === 'PICKED_UP').length}
              </div>
              <span className="text-[11px] text-blue-600/80">Courier en route to NGO</span>
            </Card>
            <Card padding="md" className="bg-surface shadow-xs border-border">
              <span className="text-xs text-emerald-700 font-medium">Completed</span>
              <div className="text-2xl font-extrabold text-emerald-700 mt-1">
                {deliveries.filter(d => d.status === 'DELIVERED').length}
              </div>
              <span className="text-[11px] text-emerald-600/80">Verified delivery handovers</span>
            </Card>
          </div>

          {/* Deliveries List */}
          {deliveries.length === 0 ? (
            <div className="text-center py-16 bg-surface rounded-2xl border border-border">
              <Truck className="w-12 h-12 text-ink-muted mx-auto mb-3 opacity-60" />
              <h3 className="text-base font-bold text-ink-primary">No Delivery Runs Dispatched Yet</h3>
              <p className="text-xs text-ink-secondary mt-1">
                When you allocate inventory for approved NGO claims, delivery runs are automatically generated here for volunteer assignment.
              </p>
            </div>
          ) : (
            <div className="space-y-4">
              {deliveries.map((delivery) => (
                <Card key={delivery.id} padding="md" className="bg-surface border border-border shadow-card flex flex-col md:flex-row md:items-start justify-between gap-5">
                  <div className="space-y-3 flex-1">
                    <div className="flex items-center gap-3 flex-wrap">
                      <span className="text-xs font-bold text-forest-800 bg-forest/10 px-2.5 py-0.5 rounded-full">
                        Run #{delivery.id}
                      </span>
                      <span className="font-extrabold text-ink-primary text-base">
                        {delivery.donation?.title || 'Surplus Batch'}
                      </span>
                      <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-forest-100 text-forest-800 border border-forest-300">
                        {delivery.status.replace('_', ' ')}
                      </span>
                      <span className="text-xs text-ink-secondary flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-ink-muted" />
                        Dispatched {new Date(delivery.createdAt).toLocaleDateString()}
                      </span>
                    </div>

                    {/* Progress Bar */}
                    <div className="w-full max-w-md">
                      <div className="flex justify-between text-xs text-ink-secondary mb-1">
                        <span>Courier Progress</span>
                        <span className="font-bold text-forest">{delivery.progressPercentage}%</span>
                      </div>
                      <div className="w-full bg-warm-muted rounded-full h-2 overflow-hidden">
                        <div
                          className="bg-forest h-2 rounded-full transition-all duration-500"
                          style={{ width: `${delivery.progressPercentage}%` }}
                        />
                      </div>
                      <p className="text-[11px] text-ink-secondary mt-1">
                        {delivery.statusDescription}
                      </p>
                    </div>

                    {/* Route Details Box */}
                    <div className="p-3.5 bg-warm-muted/70 rounded-xl text-xs space-y-2 border border-border/60">
                      <div className="flex items-center justify-between flex-wrap gap-2">
                        <div>
                          <span className="text-ink-secondary block text-[11px]">Allocated Quantity:</span>
                          <span className="text-sm font-extrabold text-forest">
                            {delivery.allocatedQuantity} {delivery.quantityUnit}
                          </span>
                        </div>
                        <div className="text-right">
                          <span className="text-ink-secondary block text-[11px]">Destination NGO:</span>
                          <span className="text-xs font-semibold text-ink-primary flex items-center gap-1">
                            <Building2 className="w-3.5 h-3.5 text-forest" />
                            {delivery.ngo?.ngoName} ({delivery.ngo?.city})
                          </span>
                        </div>
                      </div>

                      {/* Volunteer Information */}
                      <div className="border-t border-border/60 pt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-ink-secondary text-[11px]">
                        {delivery.volunteer ? (
                          <>
                            <span className="flex items-center gap-1 text-ink-primary font-semibold">
                              <User className="w-3.5 h-3.5 text-forest" />
                              Assigned Volunteer: {delivery.volunteer.fullName}
                            </span>
                            <span>Vehicle: {delivery.volunteer.vehicleType}</span>
                            <span className="flex items-center gap-1 font-mono">
                              <Phone className="w-3 h-3 text-neutral-400" />
                              {delivery.volunteer.phoneNumber}
                            </span>
                            <span>• Total Deliveries: {delivery.volunteer.totalDeliveries}</span>
                          </>
                        ) : (
                          <span className="text-amber-700 font-semibold flex items-center gap-1">
                            <AlertCircle className="w-3.5 h-3.5" />
                            Awaiting Volunteer Driver Assignment
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Actions & Pickup PIN Column */}
                  <div className="flex md:flex-col items-end justify-center gap-3 shrink-0 border-t md:border-t-0 pt-3 md:pt-0 border-border text-right w-full md:w-64">
                    {/* Pickup PIN Display for Donor Kitchen Staff */}
                    {delivery.pickupOtp && delivery.status !== 'DELIVERED' && delivery.status !== 'CANCELLED' && (
                      <div className="bg-amber-50 border border-amber-300 rounded-xl p-3 text-left w-full">
                        <span className="text-[10px] font-bold uppercase tracking-wider text-amber-900 flex items-center gap-1 mb-1">
                          <KeyRound className="w-3.5 h-3.5 text-amber-700" />
                          Kitchen Pickup PIN
                        </span>
                        <div className="flex items-center justify-between">
                          <span className="font-mono text-lg font-extrabold tracking-widest text-amber-950 bg-white px-2.5 py-0.5 rounded border border-amber-200">
                            {delivery.pickupOtp}
                          </span>
                          <span className="text-[10px] text-amber-800 max-w-[120px] text-right">
                            Give to driver upon collection
                          </span>
                        </div>
                      </div>
                    )}

                    {delivery.status === 'DELIVERED' && (
                      <div className="bg-emerald-50 border border-emerald-300 rounded-xl p-2.5 text-center text-xs font-bold text-emerald-800 flex items-center justify-center gap-1.5 w-full">
                        <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                        Handed Over to NGO
                      </div>
                    )}

                    {/* Assignment Button */}
                    {(delivery.status === 'UNASSIGNED' || delivery.status === 'PENDING_ASSIGNMENT' || delivery.status === 'REJECTED' || delivery.status === 'ASSIGNED') && (
                      <Button
                        variant={delivery.volunteer ? 'outline' : 'primary'}
                        size="sm"
                        onClick={() => handleOpenAssignModal(delivery.allocationId)}
                        className="w-full"
                        leftIcon={<Truck className="w-4 h-4" />}
                      >
                        {delivery.volunteer ? 'Reassign Volunteer' : 'Assign Volunteer'}
                      </Button>
                    )}
                  </div>
                </Card>
              ))}
            </div>
          )}
        </>
      )}

      {/* Assign Volunteer Modal */}
      {isAssignModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-surface border border-border rounded-2xl max-w-md w-full p-6 shadow-2xl">
            <div className="flex items-center justify-between border-b border-border pb-3 mb-4">
              <div className="flex items-center gap-2">
                <Truck className="w-5 h-5 text-forest" />
                <h3 className="font-bold text-ink-primary">Assign Volunteer Courier</h3>
              </div>
              <button
                onClick={() => setIsAssignModalOpen(false)}
                className="p-1 rounded-lg hover:bg-warm-muted text-ink-secondary"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleConfirmAssignment} className="space-y-4">
              <p className="text-xs text-ink-secondary">
                Select an active, verified volunteer in your cluster to fulfill this food redistribution dispatch.
              </p>

              {assignError && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-xs text-red-700 flex items-start gap-2">
                  <AlertCircle className="w-4 h-4 text-red-600 shrink-0 mt-0.5" />
                  <span>{assignError}</span>
                </div>
              )}

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Choose Available Volunteer
                </label>
                {volunteers.length === 0 ? (
                  <p className="text-xs text-ink-muted bg-warm-muted p-3 rounded-lg">
                    No active volunteers found in the cluster.
                  </p>
                ) : (
                  <select
                    value={selectedVolunteerId || ''}
                    onChange={(e) => setSelectedVolunteerId(Number(e.target.value))}
                    className="w-full text-sm px-3 py-2.5 bg-surface border border-border rounded-xl focus:ring-2 focus:ring-forest focus:outline-none"
                  >
                    {volunteers.map((vol) => (
                      <option key={vol.userId} value={vol.userId}>
                        {vol.fullName} · {vol.vehicleType} ({vol.totalDeliveries} completed runs)
                      </option>
                    ))}
                  </select>
                )}
              </div>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Kitchen Dispatch Notes (Optional)
                </label>
                <textarea
                  rows={2}
                  value={assignNotes}
                  onChange={(e) => setAssignNotes(e.target.value)}
                  placeholder="e.g. Please bring insulated hot carrier bags; report to service kitchen back door."
                  className="w-full text-xs px-3 py-2 bg-surface border border-border rounded-xl focus:ring-2 focus:ring-forest focus:outline-none"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-3 border-t border-border">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setIsAssignModalOpen(false)}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  size="sm"
                  disabled={isSubmittingAssign || volunteers.length === 0}
                  leftIcon={<Truck className="w-4 h-4" />}
                >
                  {isSubmittingAssign ? 'Assigning...' : 'Confirm Assignment'}
                </Button>
              </div>
            </form>
          </div>
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

      {/* Decline Claim Modal */}
      {rejectingRequest && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
          <div className="bg-surface border border-border rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-red-100 text-red-800 border border-red-300">
                  Decline Redistribution Claim
                </span>
                <h3 className="text-lg font-bold text-ink-primary mt-1">
                  Decline Request from {rejectingRequest.ngoName}
                </h3>
                <p className="text-xs text-ink-secondary mt-0.5">
                  Batch: {rejectingRequest.donationTitle} ({rejectingRequest.requestedQuantity} {rejectingRequest.donationQuantityUnit})
                </p>
              </div>
              <button
                onClick={() => setRejectingRequest(null)}
                disabled={isSubmittingReject}
                className="text-ink-secondary hover:text-ink-primary p-1 rounded-lg"
              >
                ✕
              </button>
            </div>

            {rejectError && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2">
                <AlertCircle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
                <span>{rejectError}</span>
              </div>
            )}

            <form onSubmit={handleConfirmReject} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-ink-primary mb-1">
                  Reason for Declining *
                </label>
                <textarea
                  rows={3}
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
                  placeholder="e.g., Cold storage requirements cannot be met for this distance, or batch already promised to nearby shelter."
                  className="w-full rounded-lg border border-border bg-surface p-3 text-xs text-ink-primary placeholder:text-ink-muted focus:outline-none focus:ring-2 focus:ring-forest/20 focus:border-forest"
                  required
                />
                <span className="text-[11px] text-ink-muted mt-1 block">
                  This explanation will be shared transparently with the requesting NGO.
                </span>
              </div>

              <div className="pt-2 border-t border-border flex items-center justify-end gap-3">
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => setRejectingRequest(null)}
                  disabled={isSubmittingReject}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  size="sm"
                  disabled={isSubmittingReject}
                  className="bg-red-700 hover:bg-red-800 text-white"
                  leftIcon={isSubmittingReject ? <RefreshCw className="w-4 h-4 animate-spin" /> : undefined}
                >
                  {isSubmittingReject ? 'Declining...' : 'Confirm Decline'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Allocation Engine Modal */}
      {allocatingRequest && (() => {
        const allocated = allocatedByRequestId[allocatingRequest.id] || 0;
        const remainingReq = Math.max(0, allocatingRequest.requestedQuantity - allocated);
        const matchingDonation = donations.find(d => d.id === allocatingRequest.donationId);
        const availStock = matchingDonation ? matchingDonation.availableQuantity : allocatingRequest.donationAvailableQuantity;
        const maxPossible = Math.min(remainingReq, availStock);

        return (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs animate-in fade-in duration-150">
            <div className="bg-surface border border-border rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4">
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 border border-emerald-300">
                    Transactional Food Allocation
                  </span>
                  <h3 className="text-lg font-bold text-ink-primary mt-1">
                    Allocate to {allocatingRequest.ngoName}
                  </h3>
                  <p className="text-xs text-ink-secondary mt-0.5">
                    Batch: {allocatingRequest.donationTitle}
                  </p>
                </div>
                <button
                  onClick={() => setAllocatingRequest(null)}
                  disabled={isSubmittingAlloc}
                  className="text-ink-secondary hover:text-ink-primary p-1 rounded-lg"
                >
                  ✕
                </button>
              </div>

              {/* Stock and Request Summary Card */}
              <div className="p-3.5 bg-warm-muted/70 rounded-xl space-y-2 text-xs border border-border/60">
                <div className="flex justify-between items-center">
                  <span className="text-ink-secondary">Approved Request Balance:</span>
                  <span className="font-extrabold text-ink-primary">
                    {remainingReq} of {allocatingRequest.requestedQuantity} {allocatingRequest.donationQuantityUnit}
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-ink-secondary">Donation Stock Available:</span>
                  <span className="font-extrabold text-forest">
                    {availStock} {allocatingRequest.donationQuantityUnit}
                  </span>
                </div>
                <div className="flex justify-between items-center border-t border-border/60 pt-1.5 text-ink-secondary">
                  <span>Max Allocatable Right Now:</span>
                  <span className="font-extrabold text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded">
                    {maxPossible} {allocatingRequest.donationQuantityUnit}
                  </span>
                </div>
              </div>

              {allocError && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-700 flex items-start gap-2">
                  <AlertCircle className="w-4 h-4 text-red-600 mt-0.5 shrink-0" />
                  <span>{allocError}</span>
                </div>
              )}

              <form onSubmit={handleConfirmAllocation} className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-ink-primary mb-1">
                    Allocation Quantity ({allocatingRequest.donationQuantityUnit}) *
                  </label>
                  <Input
                    type="number"
                    step="0.01"
                    min="0.01"
                    max={maxPossible}
                    value={allocQuantity}
                    onChange={(e) => setAllocQuantity(e.target.value)}
                    placeholder={`Max ${maxPossible}`}
                    required
                  />
                  <div className="flex items-center justify-between mt-1 text-[11px] text-ink-muted">
                    <span>Full or partial allocation authorized</span>
                    <button
                      type="button"
                      onClick={() => setAllocQuantity(maxPossible.toString())}
                      className="text-forest hover:underline font-bold"
                    >
                      Fill Max ({maxPossible})
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-ink-primary mb-1">
                    Dispatch & Staging Notes (Optional)
                  </label>
                  <Input
                    value={allocNotes}
                    onChange={(e) => setAllocNotes(e.target.value)}
                    placeholder="e.g. Staged at rear dispatch loading dock, ready for volunteer pickup."
                  />
                </div>

                <div className="pt-2 border-t border-border flex items-center justify-end gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => setAllocatingRequest(null)}
                    disabled={isSubmittingAlloc}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    size="sm"
                    disabled={isSubmittingAlloc || maxPossible <= 0}
                    leftIcon={isSubmittingAlloc ? <RefreshCw className="w-4 h-4 animate-spin" /> : <PackageCheck className="w-4 h-4" />}
                  >
                    {isSubmittingAlloc ? 'Allocating...' : 'Confirm & Deduct Stock'}
                  </Button>
                </div>
              </form>
            </div>
          </div>
        );
      })()}
    </div>
  );
};
