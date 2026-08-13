package netzero.demo.purchase.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import netzero.demo.purchase.entity.Purchase;

public record PurchaseResponse(
        Long id,
        Long foodId,
        String foodTitle,
        Integer price,
        LocalTime pickupTime,
        LocalDateTime purchasedAt,
        Integer earnedMileage
) {

    public static PurchaseResponse from(Purchase purchase, Integer earnedMileage) {
        return new PurchaseResponse(
                purchase.getId(),
                purchase.getFood().getId(),
                purchase.getFood().getTitle(),
                purchase.getPrice(),
                purchase.getPickupTime(),
                purchase.getPurchasedAt(),
                earnedMileage
        );
    }
}
