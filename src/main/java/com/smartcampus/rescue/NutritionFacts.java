package com.smartcampus.rescue;

import java.io.Serializable;

/**
 * Nutrition values for a food amount. FoodItem stores values per 100 grams;
 * this class is also used for estimates calculated for an actual portion.
 */
public final class NutritionFacts implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double caloriesKcal;
    private final double proteinGrams;
    private final double carbohydratesGrams;
    private final double fatGrams;
    private final double fibreGrams;
    private final double calciumMg;
    private final double ironMg;

    public NutritionFacts(double caloriesKcal, double proteinGrams,
                          double carbohydratesGrams, double fatGrams,
                          double fibreGrams, double calciumMg, double ironMg) {
        this.caloriesKcal = requireNonNegative("Calories", caloriesKcal);
        this.proteinGrams = requireNonNegative("Protein", proteinGrams);
        this.carbohydratesGrams = requireNonNegative("Carbohydrates", carbohydratesGrams);
        this.fatGrams = requireNonNegative("Fat", fatGrams);
        this.fibreGrams = requireNonNegative("Fibre", fibreGrams);
        this.calciumMg = requireNonNegative("Calcium", calciumMg);
        this.ironMg = requireNonNegative("Iron", ironMg);
    }

    private static double requireNonNegative(String field, double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(field + " must be a valid non-negative number.");
        }
        return value;
    }

    /** Scale nutrition values proportionally from 100 g to the chosen amount. */
    public NutritionFacts forGrams(double grams) {
        if (!Double.isFinite(grams) || grams < 0) {
            throw new IllegalArgumentException("Food weight must be a non-negative number.");
        }
        double multiplier = grams / 100.0;
        return new NutritionFacts(
                caloriesKcal * multiplier,
                proteinGrams * multiplier,
                carbohydratesGrams * multiplier,
                fatGrams * multiplier,
                fibreGrams * multiplier,
                calciumMg * multiplier,
                ironMg * multiplier
        );
    }

    /** Add two sets of nutrition values, useful for a multi-meal summary. */
    public NutritionFacts plus(NutritionFacts other) {
        return new NutritionFacts(
                caloriesKcal + other.caloriesKcal,
                proteinGrams + other.proteinGrams,
                carbohydratesGrams + other.carbohydratesGrams,
                fatGrams + other.fatGrams,
                fibreGrams + other.fibreGrams,
                calciumMg + other.calciumMg,
                ironMg + other.ironMg
        );
    }

    public static NutritionFacts zero() {
        return new NutritionFacts(0, 0, 0, 0, 0, 0, 0);
    }

    public double getCaloriesKcal() { return caloriesKcal; }
    public double getProteinGrams() { return proteinGrams; }
    public double getCarbohydratesGrams() { return carbohydratesGrams; }
    public double getFatGrams() { return fatGrams; }
    public double getFibreGrams() { return fibreGrams; }
    public double getCalciumMg() { return calciumMg; }
    public double getIronMg() { return ironMg; }

    @Override
    public String toString() {
        return String.format(
                "%.1f kcal | Protein %.1fg | Carbs %.1fg | Fat %.1fg | Fibre %.1fg | Calcium %.1fmg | Iron %.2fmg",
                caloriesKcal, proteinGrams, carbohydratesGrams, fatGrams,
                fibreGrams, calciumMg, ironMg
        );
    }
}
