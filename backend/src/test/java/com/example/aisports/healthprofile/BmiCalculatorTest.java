package com.example.aisports.healthprofile;

import com.example.aisports.common.validation.BmiCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BmiCalculatorTest {
    @Test
    void calculatesBmiWithOneDecimalPlace() {
        assertThat(BmiCalculator.calculate(175, 67)).isEqualByComparingTo(new BigDecimal("21.9"));
    }
}

