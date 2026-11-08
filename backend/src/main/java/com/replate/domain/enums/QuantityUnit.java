package com.replate.domain.enums;

public enum QuantityUnit {
    KG("Kilograms"),
    MEAL_BOXES("Meal Boxes / Thalis"),
    LITERS("Liters"),
    PORTIONS("Standard Portions");

    private final String label;

    QuantityUnit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

