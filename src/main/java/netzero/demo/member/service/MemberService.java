package netzero.demo.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import netzero.demo.member.dto.MemberTypeResponse;
import netzero.demo.member.entity.Member;
import netzero.demo.member.repository.MemberRepository;
import netzero.demo.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public MemberTypeResponse getMemberType(Long memberId) {
        boolean isRestaurantOwner = restaurantRepository.existsByOwnerId(memberId);
        return MemberTypeResponse.of(memberId, isRestaurantOwner);
    }

    @Transactional
    public Member loginWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = verify(idToken);

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String profileImageUrl = (String) payload.get("picture");

        return memberRepository.findByGoogleId(googleId)
                .map(member -> {
                    member.updateProfile(name, profileImageUrl);
                    return member;
                })
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .googleId(googleId)
                                .email(email)
                                .name(name)
                                .profileImageUrl(profileImageUrl)
                                .build()
                ));
    }

    private GoogleIdToken.Payload verify(String idToken) {
        try {
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            if (googleIdToken == null) {
                throw new IllegalArgumentException("유효하지 않은 Google ID 토큰입니다.");
            }
            return googleIdToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("Google ID 토큰 검증에 실패했습니다.", e);
        }
    }
}
