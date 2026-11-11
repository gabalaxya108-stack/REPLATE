import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DeliveryTaskResponse, DeliveryStatus } from '../types/delivery';
import {
  Truck,
  Package,
  MapPin,
  Clock,
  CheckCircle2,
  AlertTriangle,
  ShieldCheck,
  Building,
  User,
  Phone,
  XCircle,
  RefreshCw,
  KeyRound,
  Navigation,
} from 'lucide-react';

export const VolunteerDashboard: React.FC = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState<DeliveryTaskResponse[]>([]);
  const [availableTasks, setAvailableTasks] = useState<DeliveryTaskResponse[]>([]);
  const [activeTab, setActiveTab] = useState<'active' | 'available' | 'history'>('active');
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Modal States
  const [selectedTask, setSelectedTask] = useState<DeliveryTaskResponse | null>(null);
  const [modalType, setModalType] = useState<'pickup' | 'deliver' | 'reject' | 'issue' | null>(null);
  const [otpInput, setOtpInput] = useState('');
  const [notesInput, setNotesInput] = useState('');
  const [recipientName, setRecipientName] = useState('');
  const [recipientPhone, setRecipientPhone] = useState('');
  const [reasonInput, setReasonInput] = useState('');

  const loadDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);
      const [myRuns, openRuns] = await Promise.all([
        api.getMyDeliveries(),
        api.getAvailableDeliveries().catch(() => []),
      ]);
      setTasks(myRuns);
      setAvailableTasks(openRuns);
    } catch (err: any) {
      setError(err?.message || 'Failed to load delivery assignments.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDeliveries();
  }, []);

  const showNotification = (msg: string) => {
    setSuccessMessage(msg);
    setTimeout(() => setSuccessMessage(null), 5000);
  };

  const closeModal = () => {
    setSelectedTask(null);
    setModalType(null);
    setOtpInput('');
    setNotesInput('');
    setRecipientName('');
    setRecipientPhone('');
    setReasonInput('');
  };

  // Actions
  const handleAccept = async (task: DeliveryTaskResponse) => {
    try {
      setActionLoading(true);
      setError(null);
      await api.acceptDelivery(task.id);
      showNotification(`Run #${task.id} accepted! Proceed to donor kitchen for pickup.`);
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Failed to accept delivery run.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!selectedTask) return;
    try {
      setActionLoading(true);
      setError(null);
      await api.rejectDelivery(selectedTask.id, { reason: reasonInput });
      showNotification(`Run #${selectedTask.id} declined and returned to dispatch pool.`);
      closeModal();
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Failed to decline delivery run.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmPickup = async () => {
    if (!selectedTask) return;
    try {
      setActionLoading(true);
      setError(null);
      await api.confirmPickup(selectedTask.id, { otp: otpInput, notes: notesInput });
      showNotification(`Pickup confirmed for run #${selectedTask.id}! Food package in your custody.`);
      closeModal();
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Invalid verification PIN or pickup failed.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleStartTransit = async (task: DeliveryTaskResponse) => {
    try {
      setActionLoading(true);
      setError(null);
      await api.startTransit(task.id, { notes: 'Volunteer departed donor kitchen' });
      showNotification(`Run #${task.id} marked In-Transit. Proceed carefully to NGO site.`);
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Failed to start transit.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleConfirmDelivery = async () => {
    if (!selectedTask) return;
    try {
      setActionLoading(true);
      setError(null);
      await api.completeDelivery(selectedTask.id, {
        otp: otpInput,
        recipientName: recipientName.trim() || undefined,
        recipientPhone: recipientPhone.trim() || undefined,
        notes: notesInput.trim() || undefined,
      });
      showNotification(`Delivery successfully completed for run #${selectedTask.id}! Thank you for your service.`);
      closeModal();
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Invalid delivery confirmation PIN or completion failed.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReportIssue = async () => {
    if (!selectedTask) return;
    try {
      setActionLoading(true);
      setError(null);
      await api.reportDeliveryIssue(selectedTask.id, { reason: reasonInput, notes: notesInput });
      showNotification(`Issue reported for run #${selectedTask.id}. Dispatch team alerted.`);
      closeModal();
      await loadDeliveries();
    } catch (err: any) {
      setError(err?.message || 'Failed to report issue.');
    } finally {
      setActionLoading(false);
    }
  };

  // Filter tasks
  const activeRuns = tasks.filter(
    (t) => t.status === 'ASSIGNED' || t.status === 'ACCEPTED' || t.status === 'PICKED_UP' || t.status === 'IN_TRANSIT'
  );
  const historyRuns = tasks.filter(
    (t) => t.status === 'DELIVERED' || t.status === 'FAILED' || t.status === 'CANCELLED' || t.status === 'REJECTED'
  );

  const getStatusBadge = (status: DeliveryStatus) => {
    switch (status) {
      case 'ASSIGNED':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-800 border border-amber-300">New Assignment</span>;
      case 'ACCEPTED':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-800 border border-blue-300">Accepted · Go to Donor</span>;
      case 'PICKED_UP':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-100 text-indigo-800 border border-indigo-300">Food Picked Up</span>;
      case 'IN_TRANSIT':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-terracotta-100 text-terracotta-800 border border-terracotta-300 animate-pulse">In Transit</span>;
      case 'DELIVERED':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800 border border-emerald-300">Delivered</span>;
      case 'REJECTED':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-neutral-100 text-neutral-600 border border-neutral-300">Declined</span>;
      case 'FAILED':
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-100 text-rose-800 border border-rose-300">Issue Reported</span>;
      default:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-800 border border-slate-300">{status}</span>;
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header Profile / Stats */}
      <div className="bg-white rounded-2xl shadow-sm border border-neutral-200 p-6 sm:p-8 mb-8">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-xl bg-forest-100 flex items-center justify-center text-forest-700">
                <Truck className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-ink-primary">Volunteer Logistics Hub</h1>
                <p className="text-sm text-ink-secondary">
                  Welcome back, <span className="font-semibold text-forest-800">{user?.fullName || 'Field Volunteer'}</span> · REPLATE Certified Courier
                </p>
              </div>
            </div>
          </div>
          <button
            onClick={loadDeliveries}
            disabled={loading}
            className="inline-flex items-center gap-2 px-4 py-2 border border-neutral-300 rounded-xl text-sm font-medium text-ink-primary hover:bg-neutral-50 transition"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            Refresh Runs
          </button>
        </div>

        {/* Metric Cards */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6 pt-6 border-t border-neutral-100">
          <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/60">
            <span className="text-xs font-medium text-ink-secondary block">Active Deliveries</span>
            <span className="text-2xl font-bold text-forest-700">{activeRuns.length}</span>
          </div>
          <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/60">
            <span className="text-xs font-medium text-ink-secondary block">In Transit</span>
            <span className="text-2xl font-bold text-terracotta-700">
              {tasks.filter((t) => t.status === 'IN_TRANSIT').length}
            </span>
          </div>
          <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/60">
            <span className="text-xs font-medium text-ink-secondary block">Completed Deliveries</span>
            <span className="text-2xl font-bold text-emerald-700">
              {tasks.filter((t) => t.status === 'DELIVERED').length}
            </span>
          </div>
          <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/60">
            <span className="text-xs font-medium text-ink-secondary block">Cluster Open Runs</span>
            <span className="text-2xl font-bold text-blue-700">{availableTasks.length}</span>
          </div>
        </div>
      </div>

      {/* Alerts */}
      {successMessage && (
        <div className="mb-6 bg-emerald-50 border border-emerald-300 rounded-xl p-4 flex items-center gap-3 text-emerald-800 text-sm">
          <CheckCircle2 className="w-5 h-5 flex-shrink-0 text-emerald-600" />
          <span>{successMessage}</span>
        </div>
      )}

      {error && (
        <div className="mb-6 bg-rose-50 border border-rose-300 rounded-xl p-4 flex items-center gap-3 text-rose-800 text-sm">
          <AlertTriangle className="w-5 h-5 flex-shrink-0 text-rose-600" />
          <span>{error}</span>
        </div>
      )}

      {/* Tabs */}
      <div className="flex border-b border-neutral-200 mb-6">
        <button
          onClick={() => setActiveTab('active')}
          className={`py-3 px-5 text-sm font-semibold border-b-2 flex items-center gap-2 transition ${
            activeTab === 'active'
              ? 'border-forest-700 text-forest-800'
              : 'border-transparent text-ink-secondary hover:text-ink-primary'
          }`}
        >
          <Truck className="w-4 h-4" />
          My Assigned Runs ({activeRuns.length})
        </button>
        <button
          onClick={() => setActiveTab('available')}
          className={`py-3 px-5 text-sm font-semibold border-b-2 flex items-center gap-2 transition ${
            activeTab === 'available'
              ? 'border-forest-700 text-forest-800'
              : 'border-transparent text-ink-secondary hover:text-ink-primary'
          }`}
        >
          <Package className="w-4 h-4" />
          Available Runs ({availableTasks.length})
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`py-3 px-5 text-sm font-semibold border-b-2 flex items-center gap-2 transition ${
            activeTab === 'history'
              ? 'border-forest-700 text-forest-800'
              : 'border-transparent text-ink-secondary hover:text-ink-primary'
          }`}
        >
          <Clock className="w-4 h-4" />
          Run History ({historyRuns.length})
        </button>
      </div>

      {/* Main Content Area */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-16">
          <RefreshCw className="w-8 h-8 text-forest-700 animate-spin mb-3" />
          <p className="text-sm text-ink-secondary">Loading delivery assignments...</p>
        </div>
      ) : activeTab === 'active' ? (
        activeRuns.length === 0 ? (
          <div className="bg-white rounded-2xl border border-neutral-200 p-12 text-center">
            <Truck className="w-12 h-12 text-neutral-300 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-ink-primary">No active delivery assignments</h3>
            <p className="text-sm text-ink-secondary mt-1">
              You are currently free! Check the "Available Runs" tab to claim a dispatch route.
            </p>
          </div>
        ) : (
          <div className="space-y-6">
            {activeRuns.map((task) => (
              <div
                key={task.id}
                className="bg-white rounded-2xl border border-neutral-200 overflow-hidden shadow-sm hover:shadow-md transition"
              >
                {/* Card Top */}
                <div className="p-6 border-b border-neutral-100 flex flex-col md:flex-row md:items-center justify-between gap-4 bg-warm-bg/30">
                  <div>
                    <div className="flex items-center gap-3">
                      <span className="text-xs font-bold text-forest-800 bg-forest-100 px-2.5 py-0.5 rounded-full">
                        Run #{task.id}
                      </span>
                      {getStatusBadge(task.status)}
                    </div>
                    <h3 className="text-lg font-bold text-ink-primary mt-2">
                      {task.donation?.title || 'Surplus Food Batch'}
                    </h3>
                    <p className="text-xs text-ink-secondary flex items-center gap-2 mt-1">
                      <span>Quantity:</span>
                      <strong className="text-ink-primary">
                        {task.allocatedQuantity} {task.quantityUnit}
                      </strong>
                      <span>·</span>
                      <span>Category: {task.donation?.category}</span>
                    </p>
                  </div>

                  {/* Progress Meter */}
                  <div className="w-full md:w-64">
                    <div className="flex justify-between text-xs text-ink-secondary mb-1">
                      <span>Transit Progress</span>
                      <span className="font-semibold text-forest-800">{task.progressPercentage}%</span>
                    </div>
                    <div className="w-full bg-neutral-200 rounded-full h-2 overflow-hidden">
                      <div
                        className="bg-forest-600 h-2 rounded-full transition-all duration-500"
                        style={{ width: `${task.progressPercentage}%` }}
                      />
                    </div>
                    <p className="text-[11px] text-ink-secondary mt-1 truncate">
                      {task.statusDescription}
                    </p>
                  </div>
                </div>

                {/* Card Body - Route Info */}
                <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* Origin: Donor Kitchen */}
                  <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/80">
                    <div className="flex items-center gap-2 text-xs font-bold text-forest-800 uppercase tracking-wider mb-2">
                      <Building className="w-4 h-4 text-forest-600" />
                      1. Pickup Location (Donor Kitchen)
                    </div>
                    <h4 className="font-bold text-ink-primary">{task.donor?.organizationName}</h4>
                    <p className="text-sm text-ink-secondary flex items-start gap-1.5 mt-1">
                      <MapPin className="w-4 h-4 text-neutral-400 mt-0.5 flex-shrink-0" />
                      <span>
                        {task.donor?.addressLine1}, {task.donor?.city}, {task.donor?.state} - {task.donor?.pincode}
                      </span>
                    </p>
                    <div className="flex items-center gap-4 mt-3 pt-3 border-t border-neutral-200/50 text-xs text-ink-secondary">
                      <span className="flex items-center gap-1">
                        <User className="w-3.5 h-3.5 text-neutral-400" />
                        {task.donor?.contactPerson}
                      </span>
                      <span className="flex items-center gap-1 font-mono">
                        <Phone className="w-3.5 h-3.5 text-neutral-400" />
                        {task.donor?.phoneNumber}
                      </span>
                    </div>
                    {task.donation?.pickupNotes && (
                      <p className="text-xs bg-amber-50 text-amber-900 border border-amber-200 rounded p-2 mt-2">
                        <strong>Kitchen Notes:</strong> {task.donation.pickupNotes}
                      </p>
                    )}
                  </div>

                  {/* Destination: NGO Center */}
                  <div className="bg-warm-bg rounded-xl p-4 border border-neutral-200/80">
                    <div className="flex items-center gap-2 text-xs font-bold text-terracotta-800 uppercase tracking-wider mb-2">
                      <MapPin className="w-4 h-4 text-terracotta-600" />
                      2. Destination Site (Beneficiary NGO)
                    </div>
                    <h4 className="font-bold text-ink-primary">{task.ngo?.ngoName}</h4>
                    <p className="text-sm text-ink-secondary flex items-start gap-1.5 mt-1">
                      <Navigation className="w-4 h-4 text-neutral-400 mt-0.5 flex-shrink-0" />
                      <span>
                        {task.ngo?.addressLine1}, {task.ngo?.city}, {task.ngo?.state} - {task.ngo?.pincode}
                      </span>
                    </p>
                    <div className="flex items-center gap-4 mt-3 pt-3 border-t border-neutral-200/50 text-xs text-ink-secondary">
                      <span className="flex items-center gap-1">
                        <User className="w-3.5 h-3.5 text-neutral-400" />
                        {task.ngo?.contactPerson}
                      </span>
                      <span className="flex items-center gap-1 font-mono">
                        <Phone className="w-3.5 h-3.5 text-neutral-400" />
                        {task.ngo?.phoneNumber}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Card Actions Footer */}
                <div className="px-6 py-4 bg-neutral-50 border-t border-neutral-100 flex flex-wrap items-center justify-between gap-3">
                  <div className="text-xs text-ink-secondary">
                    {task.pickedUpAt && (
                      <span className="mr-3">
                        Picked up: {new Date(task.pickedUpAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      </span>
                    )}
                    {task.inTransitAt && (
                      <span>
                        In Transit: {new Date(task.inTransitAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      </span>
                    )}
                  </div>

                  <div className="flex items-center gap-2">
                    {task.status === 'ASSIGNED' && (
                      <>
                        <button
                          onClick={() => {
                            setSelectedTask(task);
                            setModalType('reject');
                          }}
                          disabled={actionLoading}
                          className="px-4 py-2 border border-neutral-300 rounded-xl text-sm font-semibold text-ink-secondary hover:bg-neutral-100 transition"
                        >
                          Decline
                        </button>
                        <button
                          onClick={() => handleAccept(task)}
                          disabled={actionLoading}
                          className="px-5 py-2 bg-forest-700 hover:bg-forest-800 text-white rounded-xl text-sm font-semibold shadow-sm transition flex items-center gap-2"
                        >
                          <CheckCircle2 className="w-4 h-4" />
                          Accept Run
                        </button>
                      </>
                    )}

                    {task.status === 'ACCEPTED' && (
                      <button
                        onClick={() => {
                          setSelectedTask(task);
                          setModalType('pickup');
                        }}
                        disabled={actionLoading}
                        className="px-5 py-2 bg-forest-700 hover:bg-forest-800 text-white rounded-xl text-sm font-semibold shadow-sm transition flex items-center gap-2"
                      >
                        <KeyRound className="w-4 h-4" />
                        Confirm Food Pickup
                      </button>
                    )}

                    {task.status === 'PICKED_UP' && (
                      <button
                        onClick={() => handleStartTransit(task)}
                        disabled={actionLoading}
                        className="px-5 py-2 bg-terracotta-700 hover:bg-terracotta-800 text-white rounded-xl text-sm font-semibold shadow-sm transition flex items-center gap-2"
                      >
                        <Truck className="w-4 h-4" />
                        Start Transit to NGO
                      </button>
                    )}

                    {task.status === 'IN_TRANSIT' && (
                      <>
                        <button
                          onClick={() => {
                            setSelectedTask(task);
                            setModalType('issue');
                          }}
                          disabled={actionLoading}
                          className="px-4 py-2 border border-rose-300 text-rose-700 rounded-xl text-sm font-semibold hover:bg-rose-50 transition"
                        >
                          Report Issue
                        </button>
                        <button
                          onClick={() => {
                            setSelectedTask(task);
                            setModalType('deliver');
                          }}
                          disabled={actionLoading}
                          className="px-5 py-2 bg-emerald-700 hover:bg-emerald-800 text-white rounded-xl text-sm font-semibold shadow-sm transition flex items-center gap-2"
                        >
                          <ShieldCheck className="w-4 h-4" />
                          Confirm Delivery at NGO
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      ) : activeTab === 'available' ? (
        availableTasks.length === 0 ? (
          <div className="bg-white rounded-2xl border border-neutral-200 p-12 text-center">
            <Package className="w-12 h-12 text-neutral-300 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-ink-primary">No open delivery runs available right now</h3>
            <p className="text-sm text-ink-secondary mt-1">
              All approved allocations currently have assigned drivers. Check back shortly!
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {availableTasks.map((task) => (
              <div
                key={task.id}
                className="bg-white rounded-2xl border border-neutral-200 p-6 shadow-sm hover:shadow-md transition flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-blue-800 bg-blue-100 px-2.5 py-0.5 rounded-full">
                      Open Run #{task.id}
                    </span>
                    <span className="text-sm font-bold text-forest-700">
                      {task.allocatedQuantity} {task.quantityUnit}
                    </span>
                  </div>
                  <h3 className="text-base font-bold text-ink-primary mt-2">
                    {task.donation?.title || 'Surplus Batch'}
                  </h3>
                  <div className="mt-4 space-y-2 text-xs text-ink-secondary">
                    <p className="flex items-center gap-1.5">
                      <Building className="w-3.5 h-3.5 text-forest-700" />
                      <strong>From:</strong> {task.donor?.organizationName} ({task.donor?.city})
                    </p>
                    <p className="flex items-center gap-1.5">
                      <MapPin className="w-3.5 h-3.5 text-terracotta-700" />
                      <strong>To:</strong> {task.ngo?.ngoName} ({task.ngo?.city})
                    </p>
                  </div>
                </div>
                <div className="mt-6 pt-4 border-t border-neutral-100 flex items-center justify-between">
                  <span className="text-[11px] text-ink-secondary">
                    Created: {new Date(task.createdAt).toLocaleDateString()}
                  </span>
                  <span className="text-xs font-semibold text-neutral-500">
                    Awaiting Donor Assignment
                  </span>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        /* History Runs */
        historyRuns.length === 0 ? (
          <div className="bg-white rounded-2xl border border-neutral-200 p-12 text-center">
            <Clock className="w-12 h-12 text-neutral-300 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-ink-primary">No completed delivery history yet</h3>
            <p className="text-sm text-ink-secondary mt-1">
              Completed and closed delivery runs will appear here for audit and record keeping.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {historyRuns.map((task) => (
              <div
                key={task.id}
                className="bg-white rounded-xl border border-neutral-200 p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-ink-secondary">Run #{task.id}</span>
                    {getStatusBadge(task.status)}
                    <span className="text-xs text-ink-secondary">·</span>
                    <span className="text-xs font-bold text-ink-primary">
                      {task.allocatedQuantity} {task.quantityUnit}
                    </span>
                  </div>
                  <h4 className="text-sm font-bold text-ink-primary mt-1">
                    {task.donation?.title} · {task.donor?.organizationName} → {task.ngo?.ngoName}
                  </h4>
                  {task.recipientName && (
                    <p className="text-xs text-emerald-800 mt-1">
                      Received by: <strong>{task.recipientName}</strong> ({task.recipientPhone || 'No contact provided'})
                    </p>
                  )}
                  {task.issueReason && (
                    <p className="text-xs text-rose-800 mt-1">
                      Reason: <em>{task.issueReason}</em>
                    </p>
                  )}
                </div>
                <div className="text-right text-xs text-ink-secondary sm:border-l sm:border-neutral-100 sm:pl-4">
                  {task.deliveredAt ? (
                    <div>
                      <span className="text-emerald-700 font-semibold block">Delivered</span>
                      <span>{new Date(task.deliveredAt).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' })}</span>
                    </div>
                  ) : (
                    <div>
                      <span className="block">{new Date(task.updatedAt).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' })}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )
      )}

      {/* MODALS */}

      {/* Pickup Modal */}
      {modalType === 'pickup' && selectedTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-neutral-200 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-neutral-100">
              <div className="flex items-center gap-2">
                <KeyRound className="w-5 h-5 text-forest-700" />
                <h3 className="font-bold text-ink-primary">Confirm Food Pickup</h3>
              </div>
              <button onClick={closeModal} className="text-neutral-400 hover:text-ink-primary">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <div className="py-4 space-y-4">
              <p className="text-sm text-ink-secondary">
                Obtain the 6-digit <strong>Pickup Verification PIN</strong> from the donor kitchen rep at{' '}
                <strong>{selectedTask.donor?.organizationName}</strong>.
              </p>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  6-Digit Pickup PIN
                </label>
                <input
                  type="text"
                  maxLength={6}
                  value={otpInput}
                  onChange={(e) => setOtpInput(e.target.value)}
                  placeholder="e.g. 492810"
                  className="w-full text-center text-xl font-mono tracking-widest px-4 py-2.5 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-forest-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Pickup Notes (Optional)
                </label>
                <textarea
                  rows={2}
                  value={notesInput}
                  onChange={(e) => setNotesInput(e.target.value)}
                  placeholder="e.g. Insulated bags verified, temperature 65°C, 30 boxes intact"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-forest-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-neutral-100">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm font-semibold text-ink-secondary hover:bg-neutral-100 rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmPickup}
                disabled={actionLoading}
                className="px-5 py-2 text-sm font-semibold bg-forest-700 hover:bg-forest-800 text-white rounded-xl shadow-sm transition flex items-center gap-2"
              >
                {actionLoading && <RefreshCw className="w-4 h-4 animate-spin" />}
                Verify & Pick Up
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Deliver Modal */}
      {modalType === 'deliver' && selectedTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-neutral-200 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-neutral-100">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-emerald-700" />
                <h3 className="font-bold text-ink-primary">Confirm Final Delivery</h3>
              </div>
              <button onClick={closeModal} className="text-neutral-400 hover:text-ink-primary">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <div className="py-4 space-y-3">
              <p className="text-sm text-ink-secondary">
                Hand over the package to <strong>{selectedTask.ngo?.ngoName}</strong> and enter their confirmation PIN.
              </p>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  6-Digit Delivery PIN
                </label>
                <input
                  type="text"
                  maxLength={6}
                  value={otpInput}
                  onChange={(e) => setOtpInput(e.target.value)}
                  placeholder="e.g. 710345"
                  className="w-full text-center text-xl font-mono tracking-widest px-4 py-2.5 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Recipient Representative Name
                </label>
                <input
                  type="text"
                  value={recipientName}
                  onChange={(e) => setRecipientName(e.target.value)}
                  placeholder="e.g. Ramesh Chandra (Distribution Manager)"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Recipient Phone (Optional)
                </label>
                <input
                  type="text"
                  value={recipientPhone}
                  onChange={(e) => setRecipientPhone(e.target.value)}
                  placeholder="e.g. 9811223344"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Delivery Notes (Optional)
                </label>
                <textarea
                  rows={2}
                  value={notesInput}
                  onChange={(e) => setNotesInput(e.target.value)}
                  placeholder="e.g. Delivered into community shelter storage, signed by staff"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-neutral-100">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm font-semibold text-ink-secondary hover:bg-neutral-100 rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmDelivery}
                disabled={actionLoading}
                className="px-5 py-2 text-sm font-semibold bg-emerald-700 hover:bg-emerald-800 text-white rounded-xl shadow-sm transition flex items-center gap-2"
              >
                {actionLoading && <RefreshCw className="w-4 h-4 animate-spin" />}
                Complete Delivery
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Decline Modal */}
      {modalType === 'reject' && selectedTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-neutral-200 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-neutral-100">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-5 h-5 text-amber-600" />
                <h3 className="font-bold text-ink-primary">Decline Delivery Assignment</h3>
              </div>
              <button onClick={closeModal} className="text-neutral-400 hover:text-ink-primary">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <div className="py-4 space-y-3">
              <p className="text-sm text-ink-secondary">
                Are you sure you want to decline run #{selectedTask.id}? It will be returned to the cluster dispatch pool for reassignment.
              </p>
              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Reason for Declining
                </label>
                <textarea
                  rows={3}
                  value={reasonInput}
                  onChange={(e) => setReasonInput(e.target.value)}
                  placeholder="e.g. Schedule conflict, distance too far, vehicle maintenance"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-amber-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-neutral-100">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm font-semibold text-ink-secondary hover:bg-neutral-100 rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={handleReject}
                disabled={actionLoading}
                className="px-5 py-2 text-sm font-semibold bg-amber-600 hover:bg-amber-700 text-white rounded-xl shadow-sm transition flex items-center gap-2"
              >
                {actionLoading && <RefreshCw className="w-4 h-4 animate-spin" />}
                Confirm Decline
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Report Issue Modal */}
      {modalType === 'issue' && selectedTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-neutral-200 animate-in fade-in zoom-in-95">
            <div className="flex items-center justify-between pb-3 border-b border-neutral-100">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-5 h-5 text-rose-600" />
                <h3 className="font-bold text-ink-primary">Report Delivery Issue</h3>
              </div>
              <button onClick={closeModal} className="text-neutral-400 hover:text-ink-primary">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <div className="py-4 space-y-3">
              <p className="text-sm text-ink-secondary">
                Report any obstacle preventing successful completion of run #{selectedTask.id}.
              </p>
              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Issue Summary
                </label>
                <input
                  type="text"
                  value={reasonInput}
                  onChange={(e) => setReasonInput(e.target.value)}
                  placeholder="e.g. Severe vehicle puncture, NGO premises closed"
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-bold text-ink-secondary uppercase mb-1">
                  Additional Details
                </label>
                <textarea
                  rows={3}
                  value={notesInput}
                  onChange={(e) => setNotesInput(e.target.value)}
                  placeholder="Provide any location details or next steps..."
                  className="w-full text-sm px-3 py-2 border border-neutral-300 rounded-xl focus:ring-2 focus:ring-rose-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-neutral-100">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm font-semibold text-ink-secondary hover:bg-neutral-100 rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={handleReportIssue}
                disabled={actionLoading || !reasonInput.trim()}
                className="px-5 py-2 text-sm font-semibold bg-rose-600 hover:bg-rose-700 text-white rounded-xl shadow-sm transition flex items-center gap-2 disabled:opacity-50"
              >
                {actionLoading && <RefreshCw className="w-4 h-4 animate-spin" />}
                Submit Report
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
