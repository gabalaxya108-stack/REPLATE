import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { FoodRequestResponse } from '../types/request';
import { AllocationResponse } from '../types/allocation';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { 
  Building2, RefreshCw, AlertCircle, CheckCircle2, Clock, 
  MapPin, Utensils, XCircle, ShieldCheck, PackageCheck, 
  Truck, FileText, Phone
} from 'lucide-react';

export const NgoDashboard: React.FC = () => {
  const { user } = useAuth();
  const [activeMainTab, setActiveMainTab] = useState<'CLAIMS' | 'ALLOCATIONS'>('CLAIMS');

  const [requests, setRequests] = useState<FoodRequestResponse[]>([]);
  const [isLoadingRequests, setIsLoadingRequests] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);

  // Allocations State
  const [allocations, setAllocations] = useState<AllocationResponse[]>([]);
  const [isLoadingAllocations, setIsLoadingAllocations] = useState(false);
  const [allocFilter, setAllocFilter] = useState<string>('ALL');

  const fetchMyRequests = useCallback(async () => {
    setIsLoadingRequests(true);
    setError(null);
    try {
      const res = await api.getMyFoodRequests(0, 50);
      setRequests(res.content);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Failed to load your food requests.');
    } finally {
      setIsLoadingRequests(false);
    }
  }, []);

  const fetchMyAllocations = useCallback(async () => {
    setIsLoadingAllocations(true);
    try {
      const res = await api.getMyAllocations(0, 50);
      setAllocations(res.content);
    } catch (err: any) {
      console.error('Failed to load allocations:', err);
    } finally {
      setIsLoadingAllocations(false);
    }
  }, []);

  useEffect(() => {
    fetchMyRequests();
    fetchMyAllocations();
  }, [fetchMyRequests, fetchMyAllocations]);

  const allocatedByRequestId = useMemo(() => {
    const map: Record<number, number> = {};
    for (const a of allocations) {
      if (a.status !== 'CANCELLED') {
        map[a.requestId] = (map[a.requestId] || 0) + Number(a.allocatedQuantity);
      }
    }
    return map;
  }, [allocations]);

  const handleWithdraw = async (id: number) => {
    if (!window.confirm('Are you sure you want to withdraw this food request? The donor will no longer review it.')) {
      return;
    }
    setActionLoadingId(id);
    setError(null);
    try {
      await api.withdrawFoodRequest(id);
      setSuccessMessage('Food request successfully withdrawn.');
      fetchMyRequests();
      setTimeout(() => setSuccessMessage(null), 4000);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Failed to withdraw food request.');
    } finally {
      setActionLoadingId(null);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Badge variant="warning">Under Review</Badge>;
      case 'APPROVED':
        return <Badge variant="available">Approved by Donor</Badge>;
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

  const getAllocationStatusBadge = (status: string) => {
    switch (status) {
      case 'AWAITING_PICKUP':
        return <Badge variant="warning">Awaiting Pickup Assignment</Badge>;
      case 'IN_TRANSIT':
        return <Badge variant="allocated">In Transit</Badge>;
      case 'DELIVERED':
        return <Badge variant="available">Delivered & Fulfilled</Badge>;
      case 'CANCELLED':
        return <Badge variant="neutral">Cancelled</Badge>;
      default:
        return <Badge variant="neutral">{status}</Badge>;
    }
  };

  const filteredRequests = requests.filter((r) => {
    if (statusFilter === 'ALL') return true;
    return r.status === statusFilter;
  });

  const filteredAllocations = allocations.filter((a) => {
    if (allocFilter === 'ALL') return true;
    return a.status === allocFilter;
  });

  const totalCount = requests.length;
  const pendingCount = requests.filter((r) => r.status === 'PENDING').length;
  const approvedCount = requests.filter((r) => r.status === 'APPROVED').length;
  const rejectedCount = requests.filter((r) => r.status === 'REJECTED').length;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Top Banner */}
      <div className="bg-surface border border-border rounded-2xl p-6 mb-8 shadow-card flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-start gap-4">
          <div className="w-14 h-14 rounded-2xl bg-blue-50 text-blue-700 flex items-center justify-center shrink-0 border border-blue-200">
            <Building2 className="w-8 h-8" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-extrabold text-ink-primary tracking-tight">
                {user?.fullName || 'NGO Distribution Console'}
              </h1>
              <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-blue-100 text-blue-800 border border-blue-300">
                Verified NGO
              </span>
            </div>
            <p className="text-xs text-ink-secondary mt-1 flex items-center gap-3">
              <span className="flex items-center gap-1">
                <MapPin className="w-3.5 h-3.5 text-forest" />
                {user?.city || 'Bengaluru'} Region
              </span>
              <span>•</span>
              <span className="flex items-center gap-1 text-emerald-700">
                <ShieldCheck className="w-3.5 h-3.5" />
                NITI Aayog Darpan Verified Organization
              </span>
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              fetchMyRequests();
              fetchMyAllocations();
            }}
            disabled={isLoadingRequests || isLoadingAllocations}
            leftIcon={<RefreshCw className={`w-4 h-4 ${isLoadingRequests || isLoadingAllocations ? 'animate-spin' : ''}`} />}
          >
            Refresh
          </Button>
          <Link to="/explore">
            <Button
              variant="primary"
              size="md"
              leftIcon={<Utensils className="w-4 h-4" />}
            >
              Browse Surplus Batches
            </Button>
          </Link>
        </div>
      </div>

      {/* Primary Dashboard Navigation Tabs */}
      <div className="flex items-center gap-3 mb-8 border-b border-border pb-4">
        <button
          onClick={() => setActiveMainTab('CLAIMS')}
          className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-sm font-extrabold transition-all ${
            activeMainTab === 'CLAIMS'
              ? 'bg-forest text-white shadow-xs'
              : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
          }`}
        >
          <FileText className="w-4 h-4" />
          <span>Redistribution Claims</span>
          <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${
            activeMainTab === 'CLAIMS' ? 'bg-white/20 text-white' : 'bg-warm-muted text-ink-primary'
          }`}>
            {requests.length}
          </span>
        </button>

        <button
          onClick={() => setActiveMainTab('ALLOCATIONS')}
          className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl text-sm font-extrabold transition-all ${
            activeMainTab === 'ALLOCATIONS'
              ? 'bg-forest text-white shadow-xs'
              : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
          }`}
        >
          <PackageCheck className="w-4 h-4" />
          <span>Committed Allocations</span>
          <span className={`text-[10px] px-2 py-0.5 rounded-full font-black ${
            activeMainTab === 'ALLOCATIONS' ? 'bg-white/20 text-white' : 'bg-warm-muted text-ink-primary'
          }`}>
            {allocations.length}
          </span>
        </button>
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

      {/* Tab 1: Redistribution Claims */}
      {activeMainTab === 'CLAIMS' && (
        <>
          {/* KPI Cards */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-ink-secondary block">Total Claims</span>
              <span className="text-2xl font-black text-ink-primary mt-1 block">{totalCount}</span>
              <span className="text-[11px] text-ink-muted">Historical requests submitted</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-amber-700 block font-medium">Under Review</span>
              <span className="text-2xl font-black text-amber-700 mt-1 block">{pendingCount}</span>
              <span className="text-[11px] text-amber-600/80">Awaiting donor review</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-emerald-700 block font-medium">Approved by Donor</span>
              <span className="text-2xl font-black text-emerald-700 mt-1 block">{approvedCount}</span>
              <span className="text-[11px] text-emerald-600/80">Authorized for allocation</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-ink-secondary block">Declined / Withdrawn</span>
              <span className="text-2xl font-black text-ink-secondary mt-1 block">{rejectedCount}</span>
              <span className="text-[11px] text-ink-muted">Unfulfilled or cancelled</span>
            </Card>
          </div>

          {/* Filter Tabs */}
          <div className="flex items-center gap-2 mb-6 border-b border-border pb-3 overflow-x-auto">
            {['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'WITHDRAWN'].map((tab) => (
              <button
                key={tab}
                onClick={() => setStatusFilter(tab)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-colors ${
                  statusFilter === tab
                    ? 'bg-forest text-white'
                    : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
                }`}
              >
                {tab === 'ALL' ? 'All Claims' : tab.charAt(0) + tab.slice(1).toLowerCase()}
              </button>
            ))}
          </div>

          {/* Requests List */}
          {isLoadingRequests ? (
            <div className="py-20 text-center">
              <RefreshCw className="w-8 h-8 text-forest animate-spin mx-auto mb-3" />
              <p className="text-sm font-medium text-ink-secondary">Loading your food redistribution claims...</p>
            </div>
          ) : filteredRequests.length === 0 ? (
            <div className="text-center py-16 bg-surface rounded-2xl border border-border">
              <Utensils className="w-12 h-12 text-ink-muted mx-auto mb-3 opacity-60" />
              <h3 className="text-base font-bold text-ink-primary">No Food Claims Found</h3>
              <p className="text-xs text-ink-secondary mt-1 mb-4">
                {statusFilter === 'ALL' 
                  ? "You haven't requested any food batches yet. Check active surplus donations to submit your first claim."
                  : `No claims currently found with status "${statusFilter}".`}
              </p>
              <Link to="/explore">
                <Button variant="primary" size="sm">
                  Discover Available Food Batches
                </Button>
              </Link>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredRequests.map((req) => {
                const allocated = allocatedByRequestId[req.id] || 0;
                const remaining = Math.max(0, req.requestedQuantity - allocated);

                return (
                  <Card key={req.id} padding="md" className="bg-surface flex flex-col md:flex-row md:items-start justify-between gap-4">
                    <div className="space-y-2.5 flex-1">
                      <div className="flex items-center gap-3 flex-wrap">
                        <span className="font-bold text-ink-primary text-base">
                          {req.donationTitle}
                        </span>
                        {getStatusBadge(req.status)}
                        <span className="text-xs text-ink-secondary flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5 text-ink-muted" />
                          Submitted {new Date(req.createdAt).toLocaleString()}
                        </span>
                      </div>

                      {/* Quantities Breakdown */}
                      <div className="p-3 bg-warm-muted/70 rounded-xl text-xs space-y-1.5 border border-border/60">
                        <div className="flex items-center justify-between flex-wrap gap-2">
                          <span className="text-ink-secondary">
                            <strong>Donor Kitchen:</strong> {req.donorName} ({req.donorCity})
                          </span>
                          <span className="text-ink-secondary">
                            Requested: <strong className="text-ink-primary">{req.requestedQuantity} {req.donationQuantityUnit}</strong>
                          </span>
                        </div>

                        {req.status === 'APPROVED' && (
                          <div className="flex items-center justify-between border-t border-border/60 pt-1.5 flex-wrap gap-2 text-[11px]">
                            <span>
                              Committed Allocation:{' '}
                              <strong className={allocated > 0 ? 'text-forest' : 'text-amber-700'}>
                                {allocated} {req.donationQuantityUnit}
                              </strong>
                            </span>
                            <span>
                              Pending Allocation:{' '}
                              <strong className={remaining > 0 ? 'text-ink-primary' : 'text-ink-muted'}>
                                {remaining} {req.donationQuantityUnit}
                              </strong>
                            </span>
                          </div>
                        )}
                      </div>

                      <div className="bg-surface p-3 rounded-lg text-xs space-y-1 border border-border">
                        <span className="font-semibold text-ink-primary block">Intended Beneficiary Purpose:</span>
                        <p className="text-ink-secondary leading-relaxed">{req.intendedBeneficiaries}</p>
                      </div>

                      {req.status === 'REJECTED' && req.rejectionReason && (
                        <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-xs text-red-700">
                          <span className="font-bold block">Donor Rejection Justification:</span>
                          <p className="mt-0.5">{req.rejectionReason}</p>
                        </div>
                      )}
                    </div>

                    {/* Action Buttons */}
                    <div className="flex md:flex-col items-end justify-center gap-2 shrink-0 border-t md:border-t-0 pt-3 md:pt-0 border-border">
                      {req.status === 'PENDING' && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleWithdraw(req.id)}
                          disabled={actionLoadingId === req.id}
                          className="text-ink-secondary hover:text-red-700 hover:border-red-300"
                          leftIcon={<XCircle className="w-3.5 h-3.5 text-red-500" />}
                        >
                          Withdraw Claim
                        </Button>
                      )}

                      {req.status === 'APPROVED' && (
                        <div className="text-right">
                          {allocated > 0 ? (
                            <span className="text-[11px] font-bold text-emerald-800 bg-emerald-100 border border-emerald-300 px-2.5 py-1 rounded-md block mb-1">
                              Allocated ({allocated} {req.donationQuantityUnit})
                            </span>
                          ) : (
                            <span className="text-[11px] font-bold text-blue-800 bg-blue-100 border border-blue-300 px-2.5 py-1 rounded-md block mb-1">
                              Approved — Awaiting Allocation
                            </span>
                          )}
                          <span className="text-[10px] text-ink-secondary block">
                            {allocated > 0 ? 'See Committed Allocations tab' : 'Donor authorizing inventory'}
                          </span>
                        </div>
                      )}
                    </div>
                  </Card>
                );
              })}
            </div>
          )}
        </>
      )}

      {/* Tab 2: Confirmed Inventory Allocations */}
      {activeMainTab === 'ALLOCATIONS' && (
        <>
          {/* Allocations KPI Cards */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-ink-secondary block font-medium">Total Allocations</span>
              <span className="text-2xl font-black text-ink-primary mt-1 block">{allocations.length}</span>
              <span className="text-[11px] text-ink-muted">Confirmed stock assignments</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-amber-700 block font-medium">Awaiting Pickup</span>
              <span className="text-2xl font-black text-amber-700 mt-1 block">
                {allocations.filter(a => a.status === 'AWAITING_PICKUP').length}
              </span>
              <span className="text-[11px] text-amber-600/80">Pending volunteer dispatch</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-blue-700 block font-medium">In Transit</span>
              <span className="text-2xl font-black text-blue-700 mt-1 block">
                {allocations.filter(a => a.status === 'IN_TRANSIT').length}
              </span>
              <span className="text-[11px] text-blue-600/80">En route to distribution point</span>
            </Card>
            <Card padding="md" className="bg-surface">
              <span className="text-xs text-emerald-700 block font-medium">Delivered</span>
              <span className="text-2xl font-black text-emerald-700 mt-1 block">
                {allocations.filter(a => a.status === 'DELIVERED').length}
              </span>
              <span className="text-[11px] text-emerald-600/80">Successfully distributed</span>
            </Card>
          </div>

          {/* Filter Tabs */}
          <div className="flex items-center gap-2 mb-6 border-b border-border pb-3 overflow-x-auto">
            {['ALL', 'AWAITING_PICKUP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'].map((tab) => (
              <button
                key={tab}
                onClick={() => setAllocFilter(tab)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-colors ${
                  allocFilter === tab
                    ? 'bg-forest text-white'
                    : 'text-ink-secondary hover:text-ink-primary hover:bg-warm-muted'
                }`}
              >
                {tab === 'ALL' ? 'All Allocations' : tab.replace('_', ' ')}
              </button>
            ))}
          </div>

          {/* Allocations List */}
          {isLoadingAllocations ? (
            <div className="py-20 text-center">
              <RefreshCw className="w-8 h-8 text-forest animate-spin mx-auto mb-3" />
              <p className="text-sm font-medium text-ink-secondary">Loading confirmed allocations from PostgreSQL...</p>
            </div>
          ) : filteredAllocations.length === 0 ? (
            <div className="text-center py-16 bg-surface rounded-2xl border border-border">
              <PackageCheck className="w-12 h-12 text-ink-muted mx-auto mb-3 opacity-60" />
              <h3 className="text-base font-bold text-ink-primary">No Confirmed Allocations Found</h3>
              <p className="text-xs text-ink-secondary mt-1 mb-4">
                {allocFilter === 'ALL'
                  ? 'Once a food donor approves and commits stock for your claim, confirmed allocations appear here.'
                  : `No allocations currently with status "${allocFilter}".`}
              </p>
            </div>
          ) : (
            <div className="space-y-4">
              {filteredAllocations.map((alloc) => (
                <Card key={alloc.id} padding="md" className="bg-surface border border-border shadow-card flex flex-col md:flex-row md:items-start justify-between gap-5">
                  <div className="space-y-3 flex-1">
                    <div className="flex items-center gap-3 flex-wrap">
                      <span className="font-extrabold text-ink-primary text-base">
                        {alloc.donationTitle}
                      </span>
                      {getAllocationStatusBadge(alloc.status)}
                      <span className="text-xs text-ink-secondary flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-ink-muted" />
                        Allocated {new Date(alloc.createdAt).toLocaleString()}
                      </span>
                    </div>

                    {/* Quantity & Donor Details Box */}
                    <div className="p-3.5 bg-warm-muted/70 rounded-xl text-xs space-y-2 border border-border/60">
                      <div className="flex items-center justify-between flex-wrap gap-2">
                        <div>
                          <span className="text-ink-secondary block text-[11px]">Committed Inventory:</span>
                          <span className="text-base font-extrabold text-forest">
                            {alloc.allocatedQuantity} {alloc.quantityUnit}
                          </span>
                        </div>
                        <div className="text-right">
                          <span className="text-ink-secondary block text-[11px]">Original Requested:</span>
                          <span className="text-xs font-semibold text-ink-primary">
                            {alloc.requestRequestedQuantity} {alloc.quantityUnit}
                          </span>
                        </div>
                      </div>

                      <div className="border-t border-border/60 pt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-ink-secondary text-[11px]">
                        <span className="flex items-center gap-1">
                          <Building2 className="w-3.5 h-3.5 text-forest" />
                          <strong>Donor:</strong> {alloc.donorName} ({alloc.donorCity})
                        </span>
                        {alloc.donorPhone && (
                          <span className="flex items-center gap-1">
                            <Phone className="w-3.5 h-3.5 text-forest" />
                            {alloc.donorPhone}
                          </span>
                        )}
                        {alloc.allocatedByName && (
                          <span>
                            • Authorized by: <strong>{alloc.allocatedByName}</strong>
                          </span>
                        )}
                      </div>
                    </div>

                    {alloc.requestIntendedBeneficiaries && (
                      <div className="p-3 bg-surface rounded-lg text-xs space-y-1 border border-border">
                        <span className="font-semibold text-ink-primary block text-[11px] uppercase tracking-wider">
                          Distribution Target:
                        </span>
                        <p className="text-ink-secondary leading-relaxed">{alloc.requestIntendedBeneficiaries}</p>
                      </div>
                    )}
                  </div>

                  <div className="flex md:flex-col items-end justify-center gap-2 shrink-0 border-t md:border-t-0 pt-3 md:pt-0 border-border text-right">
                    <span className="text-xs font-bold text-ink-primary bg-warm-card border border-border px-3 py-1.5 rounded-lg flex items-center gap-1.5">
                      <Truck className="w-4 h-4 text-forest" />
                      Step 8 Volunteer Dispatch
                    </span>
                    <span className="text-[10px] text-ink-muted">
                      Allocation ID: #{alloc.id}
                    </span>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};
