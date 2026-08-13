package netzero.demo.purchase.dto;

import java.time.LocalTime;

public record PurchaseRegisterRequest(
        Long foodId,
        LocalTime pickupTime
) {

    public PurchaseRegisterRequest {
        if (foodId == null) {
            throw new IllegalArgumentException("foodId는 필수입니다.");
        }
        if (pickupTime == null) {
            throw new IllegalArgumentException("pickupTime은 필수입니다.");
        }
    }
}
