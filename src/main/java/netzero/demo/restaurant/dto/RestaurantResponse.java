package netzero.demo.restaurant.dto;

import java.time.LocalTime;
import netzero.demo.restaurant.entity.Restaurant;

public record RestaurantResponse(
        Long id,
        String name,
        String location,
        LocalTime openTime,
        LocalTime closeTime,
        Long ownerId
) {

    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getLocation(),
                restaurant.getOpenTime(),
                restaurant.getCloseTime(),
                restaurant.getOwner().getId()
        );
    }
}
