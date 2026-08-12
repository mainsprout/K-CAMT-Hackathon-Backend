package netzero.demo.member.dto;

public record GoogleLoginRequest(String idToken) {

    public GoogleLoginRequest {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("idToken은 필수입니다.");
        }
    }
}
