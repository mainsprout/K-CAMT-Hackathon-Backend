package netzero.demo.food.entity;

import java.math.BigDecimal;

public enum FoodCategory {

    MEAT(new BigDecimal("2.5")),
    VEGE(new BigDecimal("1.2")),
    BAKERY(new BigDecimal("1.5")),
    PROCESSED(new BigDecimal("1.8")),
    DRINKS(new BigDecimal("1.3")),
    DEFAULT(new BigDecimal("1.65"));

    private final BigDecimal mileageRate;

    FoodCategory(BigDecimal mileageRate) {
        this.mileageRate = mileageRate;
    }

    public BigDecimal getMileageRate() {
        return mileageRate;
    }
}
