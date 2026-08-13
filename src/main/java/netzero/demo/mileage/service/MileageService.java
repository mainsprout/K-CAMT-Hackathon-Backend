package netzero.demo.mileage.service;

import lombok.RequiredArgsConstructor;
import netzero.demo.member.entity.Member;
import netzero.demo.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MileageService {

    private final MemberRepository memberRepository;

    public int getBalance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return member.getMileageBalance();
    }
}
