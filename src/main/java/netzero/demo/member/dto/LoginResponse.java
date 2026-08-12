package netzero.demo.member.dto;

import netzero.demo.member.entity.Member;

public record LoginResponse(Long memberId, String email, String name) {

    public static LoginResponse from(Member member) {
        return new LoginResponse(member.getId(), member.getEmail(), member.getName());
    }
}
