package netzero.demo.restaurant.dto;

import java.time.LocalTime;

public record RestaurantRegisterRequest(
        String name,
        String location,
        LocalTime openTime,
        LocalTime closeTime
) {

    public RestaurantRegisterRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("location은 필수입니다.");
        }
        if (openTime == null) {
            throw new IllegalArgumentException("openTime은 필수입니다.");
        }
        if (closeTime == null) {
            throw new IllegalArgumentException("closeTime은 필수입니다.");
        }
    }
}
