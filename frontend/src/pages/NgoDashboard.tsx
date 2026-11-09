import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { api, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { FoodRequestResponse } from '../types/request';
import { Card } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { 
  Building2, RefreshCw, AlertCircle, CheckCircle2, Clock, 
  MapPin, Utensils, XCircle, ShieldCheck 
} from 'lucide-react';

export const NgoDashboard: React.FC = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState<FoodRequestResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [actionLoadingId, setActionLoadingId] = useState<number | null>(null);

  const fetchMyRequests = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await api.getMyFoodRequests(0, 50);
      setRequests(res.content);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : 'Failed to load your food requests.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMyRequests();
  }, [fetchMyRequests]);

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

  const filteredRequests = requests.filter((r) => {
    if (statusFilter === 'ALL') return true;
    return r.status === statusFilter;
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
            onClick={fetchMyRequests}
            disabled={isLoading}
            leftIcon={<RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />}
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

      {/* KPI Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <Card padding="md" className="bg-surface">
          <span className="text-xs text-ink-secondary block">Total Claims</span>
          <span className="text-2xl font-black text-ink-primary mt-1 block">{totalCount}</span>
          <span className="text-[11px] text-ink-muted">Historical redistribution requests</span>
        </Card>
        <Card padding="md" className="bg-surface">
          <span className="text-xs text-ink-secondary block">Under Review</span>
          <span className="text-2xl font-black text-amber-700 mt-1 block">{pendingCount}</span>
          <span className="text-[11px] text-amber-600/80">Awaiting commercial donor review</span>
        </Card>
        <Card padding="md" className="bg-surface">
          <span className="text-xs text-ink-secondary block">Approved</span>
          <span className="text-2xl font-black text-emerald-700 mt-1 block">{approvedCount}</span>
          <span className="text-[11px] text-emerald-600/80">Ready for volunteer logistics</span>
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
      {isLoading ? (
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
          {filteredRequests.map((req) => (
            <Card key={req.id} padding="md" className="bg-surface flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div className="space-y-2 flex-1">
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

                <div className="text-xs text-ink-secondary flex flex-wrap items-center gap-x-4 gap-y-1">
                  <span className="flex items-center gap-1">
                    <Building2 className="w-3.5 h-3.5 text-forest" />
                    <strong>Donor:</strong> {req.donorName} ({req.donorCity})
                  </span>
                  <span>•</span>
                  <span>
                    <strong>Claim Quantity:</strong>{' '}
                    <span className="font-bold text-forest">
                      {req.requestedQuantity} {req.donationQuantityUnit}
                    </span>
                  </span>
                  <span>•</span>
                  <span>
                    <strong>Available in Batch:</strong> {req.donationAvailableQuantity} {req.donationQuantityUnit}
                  </span>
                </div>

                <div className="bg-warm-muted/70 p-3 rounded-lg text-xs space-y-1">
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
                    <span className="text-[11px] font-bold text-emerald-800 bg-emerald-100 border border-emerald-300 px-2.5 py-1 rounded-md block mb-1">
                      Ready for Allocation
                    </span>
                    <span className="text-[10px] text-ink-secondary block">
                      Volunteer pickup assignment in Step 7
                    </span>
                  </div>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
