package netzero.demo.member.dto;

import netzero.demo.member.entity.Member;
import netzero.demo.member.entity.MemberRole;

public record MemberRoleResponse(Long memberId, MemberRole role) {

    public static MemberRoleResponse from(Member member) {
        return new MemberRoleResponse(member.getId(), member.getRole());
    }
}
