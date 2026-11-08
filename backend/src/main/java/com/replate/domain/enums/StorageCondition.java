package com.replate.domain.enums;

public enum StorageCondition {
    HOT_INSULATED("Hot Insulated (Above 60°C)"),
    REFRIGERATED("Refrigerated (Below 4°C)"),
    AMBIENT("Ambient Room Temperature");

    private final String description;

    StorageCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

