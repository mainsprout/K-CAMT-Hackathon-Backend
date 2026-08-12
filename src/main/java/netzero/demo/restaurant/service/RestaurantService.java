package netzero.demo.restaurant.service;

import lombok.RequiredArgsConstructor;
import netzero.demo.member.entity.Member;
import netzero.demo.member.repository.MemberRepository;
import netzero.demo.restaurant.dto.RestaurantRegisterRequest;
import netzero.demo.restaurant.entity.Restaurant;
import netzero.demo.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Restaurant register(RestaurantRegisterRequest request, Long memberId) {
        Member owner = memberRepository.getReferenceById(memberId);

        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .location(request.location())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .owner(owner)
                .build();

        return restaurantRepository.save(restaurant);
    }
}
