package netzero.demo.purchase.controller;

import lombok.RequiredArgsConstructor;
import netzero.demo.member.security.MemberPrincipal;
import netzero.demo.purchase.dto.PurchaseRegisterRequest;
import netzero.demo.purchase.dto.PurchaseRegisterResult;
import netzero.demo.purchase.dto.PurchaseResponse;
import netzero.demo.purchase.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> register(
            @RequestBody PurchaseRegisterRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        PurchaseRegisterResult result = purchaseService.register(request, principal.getMemberId());
        return ResponseEntity.ok(PurchaseResponse.from(result.purchase(), result.earnedMileage()));
    }
}
