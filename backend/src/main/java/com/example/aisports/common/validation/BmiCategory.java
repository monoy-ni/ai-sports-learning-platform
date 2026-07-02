package com.example.aisports.common.validation;

/**
 * BMI 分类 (中国成人标准): 偏瘦 <18.5 / 正常 18.5~23.9 / 超重 24.0~27.9 / 肥胖 ≥28.0
 */
public enum BmiCategory {
    UNDERWEIGHT("偏瘦"),
    NORMAL("正常"),
    OVERWEIGHT("超重"),
    OBESE("肥胖");

    private final String label;

    BmiCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
