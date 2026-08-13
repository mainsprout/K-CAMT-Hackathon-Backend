package netzero.demo.member.dto;

public record MemberTypeResponse(Long memberId, MemberType type) {

    public static MemberTypeResponse of(Long memberId, boolean isRestaurantOwner) {
        return new MemberTypeResponse(memberId, isRestaurantOwner ? MemberType.RESTAURANT : MemberType.MEMBER);
    }
}
