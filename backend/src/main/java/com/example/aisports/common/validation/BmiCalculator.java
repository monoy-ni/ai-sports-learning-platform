package com.example.aisports.common.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BmiCalculator {
    private BmiCalculator() {
    }

    public static BigDecimal calculate(double heightCm, double weightKg) {
        double heightMeters = heightCm / 100.0;
        double bmi = weightKg / (heightMeters * heightMeters);
        return BigDecimal.valueOf(bmi).setScale(1, RoundingMode.HALF_UP);
    }
}

