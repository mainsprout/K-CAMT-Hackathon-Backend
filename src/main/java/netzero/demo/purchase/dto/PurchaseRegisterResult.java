package netzero.demo.purchase.dto;

import netzero.demo.purchase.entity.Purchase;

public record PurchaseRegisterResult(
        Purchase purchase,
        Integer earnedMileage
) {
}
