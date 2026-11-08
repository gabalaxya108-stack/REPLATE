package com.replate.domain.enums;

/**
 * Standard food classifications tailored to Indian catering & dining surpluses.
 */
public enum FoodCategory {
    COOKED_MEALS("Cooked Meals & Curries", 6),       // Safe window ~6 hours
    BREADS_ROTIS("Breads, Rotis & Naans", 12),      // Safe window ~12 hours
    DAIRY_SWEETS("Dairy & Traditional Sweets", 18), // Safe window ~18 hours
    RAW_PRODUCE("Raw Fresh Produce & Fruits", 48),  // Safe window ~48 hours
    PACKAGED_GOODS("Packaged & Dry Staples", 720);   // Safe window ~30 days

    private final String displayName;
    private final int defaultSafeHours;

    FoodCategory(String displayName, int defaultSafeHours) {
        this.displayName = displayName;
        this.defaultSafeHours = defaultSafeHours;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultSafeHours() {
        return defaultSafeHours;
    }
}
