package com.replate.domain.contract;

/**
 * Demonstrates Java OOP Interface concept.
 * Behavioral contract defining an entity whose lifecycle progress can be tracked.
 */
public interface Trackable {
    /**
     * Retrieves the current human-readable status description.
     */
    String getCurrentStatusDescription();

    /**
     * Computes the completion percentage (0 - 100) of this trackable item.
     */
    int getProgressPercentage();

    /**
     * Checks if this item has reached its terminal lifecycle state.
     */
    boolean isTerminal();
}
