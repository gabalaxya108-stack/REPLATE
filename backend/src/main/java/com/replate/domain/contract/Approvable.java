package com.replate.domain.contract;

/**
 * Demonstrates Java OOP Interface concept.
 * Behavioral contract for workflow entities that require an explicit human approval or rejection.
 */
public interface Approvable {
    /**
     * Approves the request/action with an approver identifier and optional notes.
     * @return true if state transition succeeded
     */
    boolean approve(Long approverUserId, String remarks);

    /**
     * Rejects the request/action with an explicit justification.
     * @return true if state transition succeeded
     */
    boolean reject(Long approverUserId, String rejectionReason);

    /**
     * Indicates whether this entity is still awaiting a decision.
     */
    boolean isPendingDecision();
}

