package com.example.aisports.common.validation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BmiCategoryUtilTest {

    @Test
    void classify_usesChinaThresholds() {
        assertThat(BmiCategoryUtil.classify(new BigDecimal("17.5"))).isEqualTo(BmiCategory.UNDERWEIGHT);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("18.5"))).isEqualTo(BmiCategory.NORMAL);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("23.9"))).isEqualTo(BmiCategory.NORMAL);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("24.0"))).isEqualTo(BmiCategory.OVERWEIGHT);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("27.9"))).isEqualTo(BmiCategory.OVERWEIGHT);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("28.0"))).isEqualTo(BmiCategory.OBESE);
        assertThat(BmiCategoryUtil.classify(new BigDecimal("32.5"))).isEqualTo(BmiCategory.OBESE);
    }

    @Test
    void classify_nullFallsBackToNormal() {
        assertThat(BmiCategoryUtil.classify(null)).isEqualTo(BmiCategory.NORMAL);
    }

    @Test
    void labelsAreChinese() {
        assertThat(BmiCategory.UNDERWEIGHT.getLabel()).isEqualTo("偏瘦");
        assertThat(BmiCategory.NORMAL.getLabel()).isEqualTo("正常");
        assertThat(BmiCategory.OVERWEIGHT.getLabel()).isEqualTo("超重");
        assertThat(BmiCategory.OBESE.getLabel()).isEqualTo("肥胖");
    }
}
