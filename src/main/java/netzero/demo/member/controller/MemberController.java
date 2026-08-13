package netzero.demo.member.controller;

import lombok.RequiredArgsConstructor;
import netzero.demo.member.dto.MemberRoleResponse;
import netzero.demo.member.dto.MemberRoleUpdateRequest;
import netzero.demo.member.entity.Member;
import netzero.demo.member.security.MemberPrincipal;
import netzero.demo.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/role")
    public ResponseEntity<MemberRoleResponse> updateRole(
            @RequestBody MemberRoleUpdateRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Member member = memberService.updateRole(principal.getMemberId(), request.role());
        return ResponseEntity.ok(MemberRoleResponse.from(member));
    }
}
