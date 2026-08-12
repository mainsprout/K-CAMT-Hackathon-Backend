package netzero.demo.food.dto;

import java.time.LocalTime;
import netzero.demo.food.entity.Food;

public record FoodResponse(
        Long id,
        String title,
        String imageUrl,
        Integer price,
        String description,
        Long restaurantId,
        LocalTime closingTime
) {

    public static FoodResponse from(Food food) {
        return new FoodResponse(
                food.getId(),
                food.getTitle(),
                food.getImageUrl(),
                food.getPrice(),
                food.getDescription(),
                food.getRestaurant().getId(),
                food.getRestaurant().getCloseTime()
        );
    }
}
