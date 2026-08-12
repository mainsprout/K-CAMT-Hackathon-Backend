package netzero.demo.food.dto;

import java.time.LocalTime;
import netzero.demo.food.entity.Food;

public record FoodResponse(
        Long id,
        String title,
        String imageUrl,
        Integer originalPrice,
        Integer discountRate,
        Integer discountedPrice,
        String description,
        Long restaurantId,
        LocalTime closingTime
) {

    public static FoodResponse from(Food food) {
        int discountedPrice = food.getOriginalPrice() * (100 - food.getDiscountRate()) / 100;

        return new FoodResponse(
                food.getId(),
                food.getTitle(),
                food.getImageUrl(),
                food.getOriginalPrice(),
                food.getDiscountRate(),
                discountedPrice,
                food.getDescription(),
                food.getRestaurant().getId(),
                food.getRestaurant().getCloseTime()
        );
    }
}
