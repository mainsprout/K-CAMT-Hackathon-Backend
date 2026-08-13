package netzero.demo.mileage.controller;

import lombok.RequiredArgsConstructor;
import netzero.demo.member.security.MemberPrincipal;
import netzero.demo.mileage.dto.MileageBalanceResponse;
import netzero.demo.mileage.service.MileageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/{memberId}/mileage")
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;

    @GetMapping
    public ResponseEntity<MileageBalanceResponse> getBalance(
            @PathVariable Long memberId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        if (!principal.getMemberId().equals(memberId)) {
            throw new IllegalStateException("본인의 마일리지만 조회할 수 있습니다.");
        }

        int balance = mileageService.getBalance(memberId);
        return ResponseEntity.ok(new MileageBalanceResponse(memberId, balance));
    }
}
