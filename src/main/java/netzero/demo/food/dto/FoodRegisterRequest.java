package netzero.demo.food.dto;

import netzero.demo.food.entity.FoodCategory;

public record FoodRegisterRequest(
        Long restaurantId,
        String title,
        Integer originalPrice,
        Integer discountRate,
        String description,
        FoodCategory category
) {

    public FoodRegisterRequest {
        if (restaurantId == null) {
            throw new IllegalArgumentException("restaurantId는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title은 필수입니다.");
        }
        if (originalPrice == null || originalPrice < 0) {
            throw new IllegalArgumentException("originalPrice는 0 이상이어야 합니다.");
        }
        if (discountRate == null || discountRate < 0 || discountRate > 100) {
            throw new IllegalArgumentException("discountRate는 0~100 사이여야 합니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description은 필수입니다.");
        }
        if (category == null) {
            throw new IllegalArgumentException("category는 필수입니다.");
        }
    }
}
