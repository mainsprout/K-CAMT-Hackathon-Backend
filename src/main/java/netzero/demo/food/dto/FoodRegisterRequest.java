package netzero.demo.food.dto;

public record FoodRegisterRequest(
        Long restaurantId,
        String title,
        Integer price,
        String description
) {

    public FoodRegisterRequest {
        if (restaurantId == null) {
            throw new IllegalArgumentException("restaurantId는 필수입니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title은 필수입니다.");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("price는 0 이상이어야 합니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description은 필수입니다.");
        }
    }
}
