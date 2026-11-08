package com.replate.service.impact;

import com.replate.domain.enums.FoodCategory;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Demonstrates:
 * Compile-time Polymorphism (Method Overloading).
 * Provides environmental diversion calculations (CO2e offset, meals served, monetary value saved in INR).
 */
@Component
public class ImpactCalculator {

    // Average 2.5 kg CO2e saved per 1 kg of food diverted from landfill methane emission
    private static final BigDecimal CO2E_PER_KG = new BigDecimal("2.5");
    private static final BigDecimal AVG_PORTION_WEIGHT_KG = new BigDecimal("0.45"); // 450g average meal box
    private static final BigDecimal AVG_MEAL_COST_INR = new BigDecimal("85.00");    // INR ₹85 per meal value

    /**
     * Overloaded method #1: Compute CO2 offset directly from weight in kilograms.
     */
    public BigDecimal calculateCo2Offset(BigDecimal weightInKg) {
        if (weightInKg == null || weightInKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return weightInKg.multiply(CO2E_PER_KG).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Overloaded method #2: Compute CO2 offset from meal boxes and food category heuristic.
     */
    public BigDecimal calculateCo2Offset(Integer mealBoxes, FoodCategory category) {
        if (mealBoxes == null || mealBoxes <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal estimatedKg = BigDecimal.valueOf(mealBoxes).multiply(AVG_PORTION_WEIGHT_KG);
        // Category modifier: dairy and cooked curries carry higher carbon footprint
        BigDecimal categoryMultiplier = switch (category) {
            case DAIRY_SWEETS -> new BigDecimal("1.4");
            case COOKED_MEALS -> new BigDecimal("1.2");
            case RAW_PRODUCE -> new BigDecimal("0.8");
            default -> BigDecimal.ONE;
        };
        return estimatedKg.multiply(CO2E_PER_KG).multiply(categoryMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Overloaded method #3: Calculate monetary value saved in Indian Rupees (₹).
     */
    public BigDecimal calculateValueSaved(Integer mealBoxes) {
        if (mealBoxes == null || mealBoxes <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(mealBoxes).multiply(AVG_MEAL_COST_INR).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateValueSaved(BigDecimal weightInKg) {
        if (weightInKg == null || weightInKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal estimatedMeals = weightInKg.divide(AVG_PORTION_WEIGHT_KG, 0, RoundingMode.HALF_UP);
        return estimatedMeals.multiply(AVG_MEAL_COST_INR).setScale(2, RoundingMode.HALF_UP);
    }
}

