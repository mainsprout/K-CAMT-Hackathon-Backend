package netzero.demo.purchase.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import netzero.demo.food.entity.Food;
import netzero.demo.food.entity.FoodCategory;
import netzero.demo.food.repository.FoodRepository;
import netzero.demo.member.entity.Member;
import netzero.demo.member.repository.MemberRepository;
import netzero.demo.mileage.entity.Mileage;
import netzero.demo.mileage.repository.MileageRepository;
import netzero.demo.purchase.dto.PurchaseRegisterRequest;
import netzero.demo.purchase.dto.PurchaseRegisterResult;
import netzero.demo.purchase.entity.Purchase;
import netzero.demo.purchase.repository.PurchaseRepository;
import netzero.demo.restaurant.entity.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final PurchaseRepository purchaseRepository;
    private final FoodRepository foodRepository;
    private final MemberRepository memberRepository;
    private final MileageRepository mileageRepository;

    @Transactional
    public PurchaseRegisterResult register(PurchaseRegisterRequest request, Long memberId) {
        Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Food food = foodRepository.findById(request.foodId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        if (food.getSold()) {
            throw new IllegalStateException("이미 판매된 게시물입니다.");
        }

        LocalTime pickupTime = request.pickupTime();
        Restaurant restaurant = food.getRestaurant();
        if (pickupTime.isBefore(restaurant.getOpenTime()) || pickupTime.isAfter(restaurant.getCloseTime())) {
            throw new IllegalArgumentException("픽업 시간은 가게 영업 시간 이내여야 합니다.");
        }

        int price = food.getOriginalPrice() * (100 - food.getDiscountRate()) / 100;

        Purchase purchase = Purchase.builder()
                .buyer(buyer)
                .food(food)
                .price(price)
                .pickupTime(pickupTime)
                .build();
        purchaseRepository.save(purchase);
        food.markSold();

        int earnedMileage = calculateMileage(price, food.getCategory());

        Mileage mileage = Mileage.builder()
                .member(buyer)
                .purchase(purchase)
                .amount(earnedMileage)
                .build();
        mileageRepository.save(mileage);

        buyer.earnMileage(earnedMileage);

        return new PurchaseRegisterResult(purchase, earnedMileage);
    }

    private int calculateMileage(int price, FoodCategory category) {
        return BigDecimal.valueOf(price)
                .multiply(category.getMileageRate())
                .divide(HUNDRED, 0, RoundingMode.FLOOR)
                .intValue();
    }
}
