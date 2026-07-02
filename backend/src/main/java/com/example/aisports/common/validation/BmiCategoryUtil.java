package com.example.aisports.common.validation;

import java.math.BigDecimal;

public final class BmiCategoryUtil {
    private BmiCategoryUtil() {
    }

    public static BmiCategory classify(BigDecimal bmi) {
        if (bmi == null) {
            return BmiCategory.NORMAL;
        }
        double v = bmi.doubleValue();
        if (v < 18.5) {
            return BmiCategory.UNDERWEIGHT;
        }
        if (v < 24.0) {
            return BmiCategory.NORMAL;
        }
        if (v < 28.0) {
            return BmiCategory.OVERWEIGHT;
        }
        return BmiCategory.OBESE;
    }
}
