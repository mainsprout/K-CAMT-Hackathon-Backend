package netzero.demo.member.dto;

import netzero.demo.member.entity.Member;
import netzero.demo.member.entity.MemberRole;

public record LoginResponse(Long memberId, String email, String name, MemberRole role) {

    public static LoginResponse from(Member member) {
        return new LoginResponse(member.getId(), member.getEmail(), member.getName(), member.getRole());
    }
}
